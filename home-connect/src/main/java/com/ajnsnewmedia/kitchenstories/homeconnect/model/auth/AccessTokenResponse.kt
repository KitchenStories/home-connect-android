package com.ajnsnewmedia.kitchenstories.homeconnect.model.auth

import com.ajnsnewmedia.kitchenstories.homeconnect.util.Milliseconds
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccessTokenResponse(
        @Json(name = "access_token") val accessToken: String,
        @Json(name = "expires_in") val expiresIn: Int,
        @Json(name = "refresh_token") val refreshToken: String,
)

fun AccessTokenResponse.toHomeConnectAccessToken(currentTime: Milliseconds) = HomeConnectAccessToken(
        token = this.accessToken,
        expiresAt = currentTime + expiresIn * 1_000,
        refreshToken = refreshToken,
)
