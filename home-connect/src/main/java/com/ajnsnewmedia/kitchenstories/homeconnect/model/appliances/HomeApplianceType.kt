package com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class HomeApplianceType {
    Oven,
    Unknown,
}