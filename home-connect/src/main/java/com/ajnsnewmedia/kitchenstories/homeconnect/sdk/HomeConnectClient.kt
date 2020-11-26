package com.ajnsnewmedia.kitchenstories.homeconnect.sdk

import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeAppliance
import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeApplianceType
import com.ajnsnewmedia.kitchenstories.homeconnect.model.base.HomeConnectApiRequest
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.AvailableProgram
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.StartProgramRequest

interface HomeConnectClient {

    val isAuthorized: Boolean

    suspend fun getAllHomeAppliances(ofType: HomeApplianceType? = null): List<HomeAppliance>

    suspend fun getAvailablePrograms(forApplianceId: String): List<AvailableProgram>

    fun logOutUser()

    suspend fun startProgram(forApplianceId: String, program: StartProgramRequest)

}