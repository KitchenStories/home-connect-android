package com.ajnsnewmedia.kitchenstories.homeconnect.util

import okio.IOException

internal class HomeConnectInternalError(val type: Type, message: String): IOException(message) {

    enum class Type {
        StaleAuthorization,
        NotAuthorized,
        Unspecified,
    }

}
