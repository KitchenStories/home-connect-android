package com.ajnsnewmedia.kitchenstories.homeconnect.sdk

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.AuthorizationErrorResponse
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectClientCredentials
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.toHomeConnectAccessToken
import com.ajnsnewmedia.kitchenstories.homeconnect.model.jsonadapters.HomeConnectMoshiBuilder
import com.ajnsnewmedia.kitchenstories.homeconnect.util.DefaultErrorHandler
import com.ajnsnewmedia.kitchenstories.homeconnect.util.DefaultTimeProvider
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectApiFactory
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectAuthorizationState
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectError
import com.squareup.moshi.JsonReader
import kotlinx.coroutines.suspendCancellableCoroutine
import okio.Buffer
import java.io.InputStream
import java.net.URLDecoder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal object AuthorizationDependencies {

    lateinit var baseUrl: String
    lateinit var credentials: HomeConnectClientCredentials
    lateinit var homeConnectApiFactory: HomeConnectApiFactory
    lateinit var homeConnectSecretsStore: HomeConnectSecretsStore

}

private const val AUTHORIZATION_SAVED_STATE = "AUTHORIZATION_SAVED_STATE"

// TODO move testable code without web view dependency somewhere else and write tests
// TODO make sure that no memory leaks happen here
class HomeConnectAuthorization {

    private var authorizationCode: String? = null

    /**
     * @param onRequestAccessTokenStarted This callback will be triggered when the user went through the web authorization flow, has given
     * their consent and the request for the initial access token is ongoing. Use this to e.g. show a loading indicator to keep the user
     * informed about the progress.
     */
    suspend fun authorize(webView: WebView, savedInstanceState: Bundle?, onRequestAccessTokenStarted: () -> Unit) {
        val savedState: HomeConnectAuthorizationState? = savedInstanceState?.getParcelable(AUTHORIZATION_SAVED_STATE)
        if (savedState?.authorizationCode != null) {
            loadAccessToken(savedState.authorizationCode, onRequestAccessTokenStarted)
        } else {
            val authorizationCode = initWebAuthorization(webView, savedState?.webViewState)
            loadAccessToken(authorizationCode, onRequestAccessTokenStarted)
        }
    }

    fun saveInstanceState(webView: WebView, outState: Bundle) {
        val webViewState = Bundle(1)
        webView.saveState(webViewState)
        outState.putParcelable(AUTHORIZATION_SAVED_STATE, HomeConnectAuthorizationState(webViewState, authorizationCode))
    }

    @SuppressLint("SetJavaScriptEnabled")
    private suspend fun initWebAuthorization(webView: WebView, webViewState: Bundle?): String =
            suspendCancellableCoroutine { continuation ->
                webView.settings.javaScriptEnabled = true
                webView.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (url != null && url.startsWith("https://apiclient.home-connect.com/o2c.html")) {
                            val uri = Uri.parse(url)
                            val authorizationCode = uri.parseAuthorizationCode()
                            this@HomeConnectAuthorization.authorizationCode = authorizationCode
                            if (authorizationCode != null) {
                                continuation.resume(authorizationCode)
                            } else {
                                val errorDescription = uri.parseErrorDescription()
                                val error = uri.parseError()
                                continuation.resumeWithException(HomeConnectError.getExceptionFromError(error, errorDescription, cause = null))
                            }
                        }
                    }

                    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                        super.onReceivedHttpError(view, request, errorResponse)
                        if (request != null && request.isForMainFrame) {
                            val errorDescription = errorResponse?.data?.parseErrorDescription()
                            continuation.resumeWithException(HomeConnectError.Unspecified(errorDescription, null))
                        }
                    }
                }
                webView.webChromeClient = object : WebChromeClient() {}

                if (webViewState != null) {
                    webView.restoreState(webViewState)
                } else {
                    val baseUrl = AuthorizationDependencies.baseUrl
                    val credentials = AuthorizationDependencies.credentials
                    val authUrl = "${baseUrl}security/oauth/authorize?client_id=${credentials.clientId}&response_type=code"
                    webView.loadUrl(authUrl)
                }

                continuation.invokeOnCancellation {
                    webView.webChromeClient = null
                    webView.stopLoading()
                    webView.destroy()
                }
            }

    private suspend fun loadAccessToken(authorizationCode: String, onRequestAccessTokenStarted: () -> Unit) {
        onRequestAccessTokenStarted()
        try {
            val tokenResponse = AuthorizationDependencies.homeConnectApiFactory.getHomeConnectApi().postAuthorizationCode(
                    authorizationCode = authorizationCode,
                    clientId = AuthorizationDependencies.credentials.clientId,
                    clientSecret = AuthorizationDependencies.credentials.clientSecret,
            )
            val currentTimestamp = DefaultTimeProvider().currentTimestamp
            AuthorizationDependencies.homeConnectSecretsStore.accessToken = tokenResponse.toHomeConnectAccessToken(currentTimestamp)
        } catch (e: Throwable) {
            DefaultErrorHandler().handle(e)
        }
    }

    /**
     * Parses the authorization code from the redirection url after the user has logged in and authorized the app
     * example url: https://apiclient.home-connect.com/o2c.html?code=very_nice_auth_code=3D&grant_type=authorization_code
     */
    private fun Uri.parseAuthorizationCode() = this.getQueryParameter("code")

    /**
     * Parses the error description from the redirection url after a redirect to an error url happens
     * example url: https://apiclient.home-connect.com/o2c.html?error=invalid_scope&error_description=nice+error+description
     */
    private fun Uri.parseErrorDescription() = this.getQueryParameter("error_description")?.let { URLDecoder.decode(it, "UTF-8") }

    private fun Uri.parseError() = this.getQueryParameter("error")?.let { URLDecoder.decode(it, "UTF-8") }

    /**
     * Parses the error description from the json response after an HTTP error is encountered during web authorization flow
     */
    private fun InputStream.parseErrorDescription(): String? {
        this.use { inputStream ->
            val jsonReader = JsonReader.of((Buffer().readFrom(inputStream)))
            jsonReader.use {
                val errorResponse = try {
                    HomeConnectMoshiBuilder.moshiInstance.adapter(AuthorizationErrorResponse::class.java).fromJson(it)
                } catch (e: Exception) {
                    Log.e("HomeConnect", "could not parse error json after encountering an error in the web flow", e)
                    null
                }
                return errorResponse?.description
            }
        }
    }

}