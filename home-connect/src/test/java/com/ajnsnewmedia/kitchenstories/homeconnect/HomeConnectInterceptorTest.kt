package com.ajnsnewmedia.kitchenstories.homeconnect

import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectSecretsStore
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectAccessToken
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectError
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectInternalError
import com.ajnsnewmedia.kitchenstories.homeconnect.util.TimeProvider
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.converter.moshi.MoshiConverterFactory

class HomeConnectInterceptorTest {

    @Rule
    @JvmField
    val mockServer = MockWebServer()

    @Mock
    private lateinit var homeConnectSecretsStore: HomeConnectSecretsStore

    @Mock
    private lateinit var timeProvider: TimeProvider

    private lateinit var client: OkHttpClient

    private val testAccessToken = HomeConnectAccessToken("token", expiresAt = 1_000_000L, "refresh_token")

    private fun accessTokenResponse(token: String = "new_token", expiresIn: Int = 86400, refreshToken: String = "new_refresh_token") =
            MockResponse().setResponseCode(200).setBody("""
                {
                    "access_token": "$token",
                    "expires_in": $expiresIn,
                    "refresh_token": "$refreshToken"
                }
            """)

    private fun verifyAccessTokenRequest(recordedRequest: RecordedRequest, refreshToken: String) {
        // verify that the access token request is correct
        assertEquals("grant_type=refresh_token&refresh_token=$refreshToken", recordedRequest.body.readUtf8())
        assertEquals(mockServer.url(ACCESS_TOKEN_ENDPOINT), recordedRequest.requestUrl)
    }

    private fun testRequest(path: String = "/") =
            Request.Builder().addHeader("Accept", "application/json").url(mockServer.url(path)).build()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val interceptor = HomeConnectInterceptor(
                homeConnectSecretsStore = homeConnectSecretsStore,
                converterFactory = MoshiConverterFactory.create(),
                timeProvider = timeProvider,
        )

        whenever(timeProvider.currentTimestamp).thenReturn(0L)
        whenever(homeConnectSecretsStore.accessToken).thenReturn(testAccessToken)

        client = OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(HttpLoggingInterceptor()).build()
    }

    @Test
    fun `accept header is correct`() {
        mockServer.enqueue(MockResponse())

        client.newCall(testRequest()).execute()
        val request = mockServer.takeRequest()

        assertEquals("application/vnd.bsh.sdk.v1+json", request.getHeader("Accept"))
    }

    @Test
    fun `does not add accept or authorization headers when making request to the auth endpoint`() {
        mockServer.enqueue(MockResponse())

        client.newCall(testRequest(ACCESS_TOKEN_ENDPOINT)).execute()
        val request = mockServer.takeRequest()

        assertNotEquals("application/vnd.bsh.sdk.v1+json", request.getHeader("Accept"))
        assertNull(request.getHeader("Authorization"))
    }

    @Test
    fun `throws an exception when no access token is available in the secrets store when making a standard request`() {
        whenever(homeConnectSecretsStore.accessToken).thenReturn(null)
        mockServer.enqueue(MockResponse())

        try {
            client.newCall(testRequest()).execute()
            throw AssertionError("No exception has been thrown")
        } catch (e: Throwable) {
            assertTrue(e is HomeConnectInternalError)
        }
    }

    @Test
    fun `uses access token from secrets store for standard requests`() {
        whenever(homeConnectSecretsStore.accessToken).thenReturn(testAccessToken)
        mockServer.enqueue(MockResponse())

        client.newCall(testRequest()).execute()
        val request = mockServer.takeRequest()

        assertEquals("Bearer ${testAccessToken.token}", request.getHeader("Authorization"))
    }

    @Test
    fun `refreshes access token when token in secrets store has expired`() {
        val testTimestamp = 10_001L
        whenever(homeConnectSecretsStore.accessToken).thenReturn(testAccessToken.copy(expiresAt = 10_000L))
        whenever(timeProvider.currentTimestamp).thenReturn(testTimestamp)
        mockServer.enqueue(accessTokenResponse(token = "new_access_token", expiresIn = 10_000, refreshToken = "updated_refresh_token"))
        mockServer.enqueue(MockResponse())

        client.newCall(testRequest("/original_request")).execute()
        val accessTokenRequest = mockServer.takeRequest()

        // verify that the access token request is correct
        verifyAccessTokenRequest(accessTokenRequest, refreshToken = testAccessToken.refreshToken)

        verify(homeConnectSecretsStore).accessToken = HomeConnectAccessToken(
                token = "new_access_token",
                expiresAt = testTimestamp + 10_000 * 1000,
                refreshToken = "updated_refresh_token",
        )

        // verify that original request is run with refreshed token
        val requestWithRenewedToken = mockServer.takeRequest()
        assertEquals("Bearer new_access_token", requestWithRenewedToken.getHeader("Authorization"))
        assertEquals(mockServer.url("/original_request"), requestWithRenewedToken.requestUrl)
    }

    @Test
    fun `throws a stale authorization error and deletes access token from store when expired token can't be refreshed due to a 400`() {
        val testTimestamp = 10_001L
        whenever(homeConnectSecretsStore.accessToken).thenReturn(testAccessToken.copy(expiresAt = 10_000L))
        whenever(timeProvider.currentTimestamp).thenReturn(testTimestamp)
        // mock failing access token request
        mockServer.enqueue(MockResponse().setResponseCode(400))

        try {
            client.newCall(testRequest()).execute()
            throw AssertionError("No exception has been thrown")
        } catch (e: Throwable) {
            assertTrue(e is HomeConnectInternalError)
            assertEquals((e as HomeConnectInternalError).type, HomeConnectInternalError.Type.StaleAuthorization)
            verify(homeConnectSecretsStore).accessToken = null
        }
    }

    @Test
    fun `throws a stale authorization error and deletes access token from store when expired token can't be refreshed due to a 403`() {
        val testTimestamp = 10_001L
        whenever(homeConnectSecretsStore.accessToken).thenReturn(testAccessToken.copy(expiresAt = 10_000L))
        whenever(timeProvider.currentTimestamp).thenReturn(testTimestamp)
        // mock failing access token request
        mockServer.enqueue(MockResponse().setResponseCode(403))

        try {
            client.newCall(testRequest()).execute()
            throw AssertionError("No exception has been thrown")
        } catch (e: Throwable) {
            assertTrue(e is HomeConnectInternalError)
            assertEquals((e as HomeConnectInternalError).type, HomeConnectInternalError.Type.StaleAuthorization)
            verify(homeConnectSecretsStore).accessToken = null
        }
    }

    @Test
    fun `throws an unspecified error when expired token can't be refreshed due to another error code`() {
        val testTimestamp = 10_001L
        whenever(homeConnectSecretsStore.accessToken).thenReturn(testAccessToken.copy(expiresAt = 10_000L))
        whenever(timeProvider.currentTimestamp).thenReturn(testTimestamp)
        // mock failing access token request
        mockServer.enqueue(MockResponse().setResponseCode(500))

        try {
            client.newCall(testRequest()).execute()
            throw AssertionError("No exception has been thrown")
        } catch (e: Throwable) {
            assertTrue(e is HomeConnectInternalError)
            assertEquals((e as HomeConnectInternalError).type, HomeConnectInternalError.Type.Unspecified)
            verify(homeConnectSecretsStore, never()).accessToken = null
        }
    }

    @Test
    fun `renews access token when initial request fails with a 401`() {
        val testTimestamp = 10_000L
        whenever(homeConnectSecretsStore.accessToken).thenReturn(testAccessToken)
        whenever(timeProvider.currentTimestamp).thenReturn(testTimestamp)
        mockServer.enqueue(MockResponse().setResponseCode(401))
        mockServer.enqueue(accessTokenResponse(token = "new_token", expiresIn = 86400, refreshToken = "new_refresh_token"))
        mockServer.enqueue(MockResponse().setResponseCode(200))

        client.newCall(testRequest("/original_request")).execute()
        // take the failing request
        mockServer.takeRequest()

        val accessTokenRequest = mockServer.takeRequest()
        verifyAccessTokenRequest(accessTokenRequest, refreshToken = testAccessToken.refreshToken)

        verify(homeConnectSecretsStore).accessToken = HomeConnectAccessToken(
                token = "new_token",
                expiresAt = testTimestamp + 86400 * 1000,
                refreshToken = "new_refresh_token",
        )

        // verify that original request is run with refreshed token
        val requestWithRenewedToken = mockServer.takeRequest()
        assertEquals("Bearer new_token", requestWithRenewedToken.getHeader("Authorization"))
        assertEquals(mockServer.url("/original_request"), requestWithRenewedToken.requestUrl)
    }

}