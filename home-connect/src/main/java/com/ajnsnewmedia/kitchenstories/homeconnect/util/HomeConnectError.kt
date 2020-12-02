package com.ajnsnewmedia.kitchenstories.homeconnect.util

sealed class HomeConnectError(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {

    object Network : HomeConnectError("Network error encountered")

    object NotAuthorized : HomeConnectError("User is not authorized yet")

    object StaleAuthorization : HomeConnectError("The user session needs to be restored")

    class Unspecified(message: String?, cause: Throwable?) : HomeConnectError(message, cause)

    class UserAbortedAuthorization(errorDescription: String?): HomeConnectError("User aborted the login: '${errorDescription}'")

    companion object{
        fun getExceptionFromError(errorString: String?, errorDescription: String?, cause: Throwable?): HomeConnectError {
            //documentation from https://developer.home-connect.com/docs/authorization/authorizationerrors
            return if (errorString == "access_denied"){
                UserAbortedAuthorization(errorDescription)
            } else {
                Unspecified(errorDescription, cause = cause)
            }
        }
    }
}
