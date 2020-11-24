package com.ajnsnewmedia.kitchenstories.homeconnect.util

sealed class HomeConnectError(message: String? = null, cause: Throwable? = null) : Throwable(message, cause) {

    object Network : HomeConnectError("Network error encountered")

    object NotInitialized : HomeConnectError("HomeConnect SDK is not initialized")

    object NotAuthorized : HomeConnectError("User is not authorized yet")

    object StaleAuthorization : HomeConnectError("The user session needs to be restored")

    class Unspecified(message: String?, cause: Throwable?) : HomeConnectError(message, cause)

}
