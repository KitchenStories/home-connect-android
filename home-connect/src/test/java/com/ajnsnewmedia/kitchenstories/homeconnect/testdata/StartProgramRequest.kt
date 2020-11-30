package com.ajnsnewmedia.kitchenstories.homeconnect.testdata

import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.ProgramKey
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.ProgramOptionKey
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.StartProgramOption
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.StartProgramRequest

val testStartProgramRequest =
        StartProgramRequest(ProgramKey.PreHeating, listOf(StartProgramOption(ProgramOptionKey.SetpointTemperature, 100, "°C")))