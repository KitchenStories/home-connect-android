package com.ajnsnewmedia.kitchenstories.homeconnect.model.base

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class HomeConnectApiErrorResponse(val error: HomeConnectApiError)

@JsonClass(generateAdapter = true)
data class HomeConnectApiError(val key: String, val description: String)