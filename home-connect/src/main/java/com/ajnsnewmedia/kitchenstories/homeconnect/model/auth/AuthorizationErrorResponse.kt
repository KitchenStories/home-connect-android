package com.ajnsnewmedia.kitchenstories.homeconnect.model.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthorizationErrorResponse(
        val error: String,
        @Json(name = "error_description") val description: String,
)