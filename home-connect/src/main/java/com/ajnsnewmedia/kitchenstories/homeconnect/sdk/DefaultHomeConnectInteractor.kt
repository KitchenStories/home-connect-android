package com.ajnsnewmedia.kitchenstories.homeconnect.sdk

import android.util.Log
import com.ajnsnewmedia.kitchenstories.homeconnect.HomeConnectApi
import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeAppliance
import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeApplianceType
import com.ajnsnewmedia.kitchenstories.homeconnect.model.base.HomeConnectApiRequest
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.AvailableProgram
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.StartProgramRequest
import com.ajnsnewmedia.kitchenstories.homeconnect.util.ErrorHandler
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectApiFactory

internal class DefaultHomeConnectInteractor(
    private val homeConnectApiFactory: HomeConnectApiFactory,
    private val homeConnectSecretsStore: HomeConnectSecretsStore,
    private val errorHandler: ErrorHandler,
) : HomeConnectClient {

    private val homeConnectApi: HomeConnectApi by lazy { homeConnectApiFactory.getHomeConnectApi() }

    override val isAuthorized: Boolean
        get() = homeConnectSecretsStore.accessToken != null

    override suspend fun getAllHomeAppliances(ofType: HomeApplianceType?): List<HomeAppliance> {
        try {
            val allAppliances = homeConnectApi.getAllHomeAppliances().data.homeappliances
            return if (ofType != null) {
                allAppliances.filter { it.type == ofType }
            } else {
                allAppliances
            }
        } catch (e: Throwable) {
            Log.e("HomeConnectApi", "getAllHomeAppliances failed", e)
            errorHandler.handle(e)
        }
    }

    override suspend fun getAvailablePrograms(forApplianceId: String): List<AvailableProgram> {
        return try {
            homeConnectApi.getAvailablePrograms(forApplianceId).data.programs
        } catch (e: Throwable) {
            Log.e("HomeConnectApi", "getAllHomeAppliances failed", e)
            errorHandler.handle(e)
        }
    }

    override suspend fun startProgram(forApplianceId: String, program: StartProgramRequest) {
        try {
            homeConnectApi.startProgram(forApplianceId, HomeConnectApiRequest(program))
        } catch (e: Throwable) {
            Log.e("HomeConnectApi", "starting a program failed", e)
        }
    }

}

