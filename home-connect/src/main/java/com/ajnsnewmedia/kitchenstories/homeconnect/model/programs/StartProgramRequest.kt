package com.ajnsnewmedia.kitchenstories.homeconnect.model.programs

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StartProgramRequest(val key: String, val options: List<StartProgramOption>)

@JsonClass(generateAdapter = true)
data class StartProgramOption(val key: String, val value: Int, val unit: String)
