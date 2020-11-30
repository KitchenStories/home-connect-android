package com.ajnsnewmedia.kitchenstories.homeconnect.util

import com.ajnsnewmedia.kitchenstories.homeconnect.verifyThrowing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

class DefaultErrorHandlerTest {

    @Test
    fun `maps HttpExceptions to an unspecified error with the error message`() {
        val httpException = HttpException(Response.success(null))

        verifyThrowing(action = { DefaultErrorHandler().handle(httpException) }, verifyError = { error ->
            assertTrue(error is HomeConnectError.Unspecified)
            assertEquals(httpException.message, error.message)
            assertEquals(httpException, error.cause)
        })
    }

    @Test
    fun `maps UnknownHostException to a network error`() {
        val unknownHostException = UnknownHostException()

        verifyThrowing(action = { DefaultErrorHandler().handle(unknownHostException) }, verifyError = { error ->
            assertTrue(error is HomeConnectError.Network)
        })
    }

    @Test
    fun `maps internal error due to not being authorized to a NotAuthorized error`() {
        val internalError = HomeConnectInternalError(HomeConnectInternalError.Type.NotAuthorized, "message")

        verifyThrowing(action = { DefaultErrorHandler().handle(internalError) }, verifyError = { error ->
            assertTrue(error is HomeConnectError.NotAuthorized)
        })
    }

    @Test
    fun `maps internal error due to stale authorization to a StaleAuthorization error`() {
        val internalError = HomeConnectInternalError(HomeConnectInternalError.Type.StaleAuthorization, "message")

        verifyThrowing(action = { DefaultErrorHandler().handle(internalError) }, verifyError = { error ->
            assertTrue(error is HomeConnectError.StaleAuthorization)
        })
    }

    @Test
    fun `maps unspecified internal error to an unspecified error with the error message`() {
        val internalError = HomeConnectInternalError(HomeConnectInternalError.Type.Unspecified, "message")

        verifyThrowing(action = { DefaultErrorHandler().handle(internalError) }, verifyError = { error ->
            assertTrue(error is HomeConnectError.Unspecified)
            assertEquals("message", error.message)
        })
    }

}