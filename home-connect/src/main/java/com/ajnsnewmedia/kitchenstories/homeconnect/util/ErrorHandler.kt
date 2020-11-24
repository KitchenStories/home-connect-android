package com.ajnsnewmedia.kitchenstories.homeconnect.util

import retrofit2.HttpException
import java.net.UnknownHostException

internal interface ErrorHandler {

    fun handle(error: Throwable): Nothing

}

internal class DefaultErrorHandler : ErrorHandler {

    override fun handle(error: Throwable): Nothing {
        val mappedError = when (error) {
            is UnknownHostException -> HomeConnectError.Network
            is HomeConnectInternalError -> when (error.type) {
                HomeConnectInternalError.Type.NotAuthorized -> HomeConnectError.NotAuthorized
                HomeConnectInternalError.Type.StaleAuthorization -> HomeConnectError.StaleAuthorization
                HomeConnectInternalError.Type.Unspecified -> HomeConnectError.Unspecified(error.message, error)
            }
            is HttpException -> HomeConnectError.Unspecified(error.message, error)
            else -> null
        }
        throw mappedError ?: error
    }

}
