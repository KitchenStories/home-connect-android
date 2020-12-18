package com.ajnsnewmedia.kitchenstories.homeconnect.testdata

import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.AvailableProgram
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.ProgramConstraint
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.ProgramExecutionConstraint
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.ProgramKey

val testAvailableProgram = AvailableProgram(ProgramKey.TopBottomHeating,"just a name" , ProgramConstraint(ProgramExecutionConstraint.none))