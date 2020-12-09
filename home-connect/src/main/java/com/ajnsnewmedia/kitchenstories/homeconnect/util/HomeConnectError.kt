package com.ajnsnewmedia.kitchenstories.homeconnect.util

sealed class HomeConnectError(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {

    object Network : HomeConnectError("Network error encountered")

    object NotAuthorized : HomeConnectError("User is not authorized yet")

    object StaleAuthorization : HomeConnectError("The user session needs to be restored")

    class StartProgramIssue(val errorKey: String, message: String?, cause: Throwable?) : HomeConnectError(message, cause)

    class Unspecified(message: String?, cause: Throwable?) : HomeConnectError(message, cause)

    abstract class UserAbortedAuthorization(errorDescription: String?) : HomeConnectError("User aborted the login: '${errorDescription}'")

    /**
     * the user pressed cancel after logging in when reviewing the app permissions
     */
    class UserAbortedAuthorizationWhileGrantingPermission(errorDescription: String?) : UserAbortedAuthorization(errorDescription)

    /**
     * The user pressed cancel on the username and password entry page
     */
    class UserAbortedAuthorizationOnLogin(errorDescription: String?) : UserAbortedAuthorization(errorDescription)

    companion object {
        fun getExceptionFromError(errorString: String?, errorDescription: String?, cause: Throwable?): HomeConnectError {
            //documentation from https://developer.home-connect.com/docs/authorization/authorizationerrors
            return if (errorString == "access_denied") {
                if (errorDescription == "login aborted by the user") {
                    UserAbortedAuthorizationOnLogin(errorDescription)
                } else if (errorDescription == "grant operation aborted by the user") {
                    UserAbortedAuthorizationWhileGrantingPermission(errorDescription)
                } else {
                    Unspecified(errorDescription, cause = cause)
                }
            } else {
                Unspecified(errorDescription, cause = cause)
            }
        }
    }
}
