package com.ajnsnewmedia.kitchenstories.homeconnect

import android.util.Log
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.AccessTokenResponse
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.AuthorizationErrorResponse
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectAccessToken
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.toHomeConnectAccessToken
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectSecretsStore
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectInternalError
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectInternalError.Type.NotAuthorized
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectInternalError.Type.StaleAuthorization
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectInternalError.Type.Unspecified
import com.ajnsnewmedia.kitchenstories.homeconnect.util.TimeProvider
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.converter.moshi.MoshiConverterFactory

internal const val ACCESS_TOKEN_ENDPOINT = "/security/oauth/token"

class HomeConnectInterceptor(
        private val homeConnectSecretsStore: HomeConnectSecretsStore,
        private val converterFactory: MoshiConverterFactory,
        private val timeProvider: TimeProvider,
) : Interceptor {

    @Suppress("UNCHECKED_CAST")
    private val accessTokenBodyConverter: Converter<ResponseBody, AccessTokenResponse> by lazy {
        (converterFactory.responseBodyConverter(
                AccessTokenResponse::class.java,
                arrayOf(),
                null,
        ) as? Converter<ResponseBody, AccessTokenResponse>)
                ?: throw IllegalStateException("could not obtain AccessTokenResponseBodyConverter")
    }

    @Suppress("UNCHECKED_CAST")
    private val authorizationErrorBodyConverter by lazy {
        (converterFactory.responseBodyConverter(
                AuthorizationErrorResponse::class.java,
                arrayOf(),
                null,
        ) as? Converter<ResponseBody, AuthorizationErrorResponse>)
                ?: throw IllegalStateException("could not obtain AuthorizationErrorResponseBodyConverter")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (chain.request().url.encodedPath == ACCESS_TOKEN_ENDPOINT) {
            return chain.proceed(chain.request())
        }

        val accessToken = homeConnectSecretsStore.accessToken ?: throw HomeConnectInternalError(
            type = NotAuthorized,
            message = "access token is null. user session must be restored before using HomeConnectApi",
        )
        val token = if (accessToken.isExpired) {
            chain.renewAccessToken(accessToken.refreshToken).token
        } else {
            accessToken.token
        }

        val requestWithHeaders = chain.request().newBuilder().acceptHeader().header("Authorization", "Bearer $token").build()

        val response = chain.proceed(requestWithHeaders)
        if (response.code == 401) {
            Log.d("HomeConnectInterceptor", "Access token is invalid - trying to renew")
            response.close()
            val renewedToken = chain.renewAccessToken(accessToken.refreshToken)
            return chain.proceed(requestWithHeaders.newBuilder().header("Authorization", "Bearer ${renewedToken.token}").build())
        }
        return response
    }

    private fun Interceptor.Chain.renewAccessToken(refreshToken: String): HomeConnectAccessToken {
        val requestBody = FormBody.Builder().addEncoded("grant_type", "refresh_token").addEncoded("refresh_token", refreshToken).build()
        val request = Request.Builder()
                .url(this.request().url.newBuilder().encodedPath(ACCESS_TOKEN_ENDPOINT).build())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build()

        this.proceed(request).use { response ->
            if (response.isSuccessful) {
                val refreshedToken = response.body?.parseHomeConnectAccessToken() ?: throw HomeConnectInternalError(
                        type = Unspecified,
                        message = "Error parsing access token response. This is a bug in the Home Connect SDK.",
                )
                homeConnectSecretsStore.accessToken = refreshedToken
                return refreshedToken
            }

            if (response.code in 400..499) {
                homeConnectSecretsStore.accessToken = null
                val errorResponse = response.body?.parseAuthorizationErrorResponse()
                throw HomeConnectInternalError(
                        type = StaleAuthorization,
                        message = "Access token could not be refreshed. The user will be logged out. Cause: $errorResponse",
                )
            } else {
                throw HomeConnectInternalError(type = Unspecified, message = "Access token could not be refreshed. Please try again.")
            }
        }
    }

    private fun Request.Builder.acceptHeader() = this.header("Accept", "application/vnd.bsh.sdk.v1+json")

    private fun ResponseBody.parseHomeConnectAccessToken() = try {
        accessTokenBodyConverter.convert(this)?.toHomeConnectAccessToken(timeProvider.currentTimestamp)
    } catch (e: Throwable) {
        null
    }

    private fun ResponseBody.parseAuthorizationErrorResponse() = try {
        authorizationErrorBodyConverter.convert(this)
    } catch (e: Throwable) {
        Log.e("HomeConnectInterceptor", "could not parse authorization error response body", e)
    }

    private val HomeConnectAccessToken.isExpired: Boolean
        get() = timeProvider.currentTimestamp >= this.expiresAt

}
