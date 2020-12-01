package com.ajnsnewmedia.kitchenstories.homeconnect.util

sealed class HomeConnectError(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {

    object Network : HomeConnectError("Network error encountered")

    object NotAuthorized : HomeConnectError("User is not authorized yet")

    object StaleAuthorization : HomeConnectError("The user session needs to be restored")

    class Unspecified(message: String?, cause: Throwable?) : HomeConnectError(message, cause)

}
