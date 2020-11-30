package com.ajnsnewmedia.kitchenstories.homeconnect.model.programs

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class ProgramOptionKey {
    @Json(name = "Cooking.Oven.Option.SetpointTemperature") SetpointTemperature,
    @Json(name = "BSH.Common.Option.Duration") Duration,
    @Json(name = "Cooking.Oven.Option.FastPreHeat") FastPreHeat,
    @Json(name = "BSH.Common.Option.StartInRelative") StartInRelative,
    Unknown,
}