package com.ajnsnewmedia.kitchenstories.homeconnect.model.base

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeConnectApiResponse<T>(val data: T)
