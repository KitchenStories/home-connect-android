package com.ajnsnewmedia.kitchenstories.homeconnect.model.programs

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StartProgramRequest(val key: ProgramKey, val options: List<StartProgramOption>)

@JsonClass(generateAdapter = true)
data class StartProgramOption(val key: ProgramOptionKey, val value: Int, val unit: String)
