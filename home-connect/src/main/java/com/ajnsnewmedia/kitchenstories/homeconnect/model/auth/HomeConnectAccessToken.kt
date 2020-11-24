package com.ajnsnewmedia.kitchenstories.homeconnect.model.auth

data class HomeConnectAccessToken(
    val token: String,
    val expiresAt: Long,
    val refreshToken: String,
)
