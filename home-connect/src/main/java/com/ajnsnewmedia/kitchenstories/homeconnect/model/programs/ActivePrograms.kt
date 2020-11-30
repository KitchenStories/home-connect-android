package com.ajnsnewmedia.kitchenstories.homeconnect.model.programs

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ActiveProgram(val key: ProgramKey, val name: String = "", val options: List<ActiveProgramOption> = listOf())

@JsonClass(generateAdapter = true)
data class ActiveProgramOption(val key: ProgramOptionKey, val value: Int, val name: String = "", val unit: String = "")
