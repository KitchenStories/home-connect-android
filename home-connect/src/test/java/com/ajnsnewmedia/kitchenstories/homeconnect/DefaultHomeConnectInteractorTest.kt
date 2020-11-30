package com.ajnsnewmedia.kitchenstories.homeconnect

import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeApplianceType
import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeAppliancesData
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectAccessToken
import com.ajnsnewmedia.kitchenstories.homeconnect.model.base.HomeConnectApiRequest
import com.ajnsnewmedia.kitchenstories.homeconnect.model.base.HomeConnectApiResponse
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.AvailableProgramsData
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.ProgramKey
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.StartProgramRequest
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.DefaultHomeConnectInteractor
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectSecretsStore
import com.ajnsnewmedia.kitchenstories.homeconnect.testdata.testAvailableProgram
import com.ajnsnewmedia.kitchenstories.homeconnect.testdata.testHomeAppliance
import com.ajnsnewmedia.kitchenstories.homeconnect.testdata.testStartProgramRequest
import com.ajnsnewmedia.kitchenstories.homeconnect.util.ErrorHandler
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectApiFactory
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DefaultHomeConnectInteractorTest {

    //<editor-fold desc="Mocks">

    @Mock
    private lateinit var homeConnectApi: HomeConnectApi

    @Mock
    private lateinit var homeConnectApiFactory: HomeConnectApiFactory

    @Mock
    private lateinit var homeConnectSecretsStore: HomeConnectSecretsStore

    @Mock
    private lateinit var errorHandler: ErrorHandler

    //</editor-fold>

    private lateinit var interactor: DefaultHomeConnectInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        whenever(homeConnectApiFactory.getHomeConnectApi()).thenReturn(homeConnectApi)

        interactor = DefaultHomeConnectInteractor(homeConnectApiFactory, homeConnectSecretsStore, errorHandler)
    }

    //<editor-fold desc="Authorization">

    @Test
    fun `user is authorized when there is an access token in the secrets store`() {
        whenever(homeConnectSecretsStore.accessToken).thenReturn(HomeConnectAccessToken("token", 100, "refresh_token"))

        assertTrue(interactor.isAuthorized)
    }

    @Test
    fun `user is not authorized when there is no access token in the secrets store`() {
        whenever(homeConnectSecretsStore.accessToken).thenReturn(null)

        assertFalse(interactor.isAuthorized)
    }

    @Test
    fun `logging out the user removes access token from secrets store`() {

        interactor.logOutUser()

        verify(homeConnectSecretsStore).accessToken = null
    }

    //</editor-fold>

    @Test
    fun `getAllHomeAppliances without a filter returns all loaded appliances`() = runBlockingTest {
        whenever(homeConnectApi.getAllHomeAppliances()).thenReturn(HomeConnectApiResponse(HomeAppliancesData(listOf(
                testHomeAppliance,
                testHomeAppliance.copy(type = HomeApplianceType.Unknown),
        ))))

        val result = interactor.getAllHomeAppliances(ofType = null)

        assertEquals(result, listOf(testHomeAppliance, testHomeAppliance.copy(type = HomeApplianceType.Unknown)))
    }

    @Test
    fun `getAllHomeAppliances with a filter returns all loaded appliances of the type of given filter`() = runBlockingTest {
        whenever(homeConnectApi.getAllHomeAppliances()).thenReturn(HomeConnectApiResponse(HomeAppliancesData(listOf(
                testHomeAppliance.copy(type = HomeApplianceType.Oven),
                testHomeAppliance.copy(type = HomeApplianceType.Unknown),
        ))))

        val result = interactor.getAllHomeAppliances(ofType = HomeApplianceType.Oven)

        assertEquals(result, listOf(testHomeAppliance.copy(type = HomeApplianceType.Oven)))
    }

    @Test
    fun `getAvailablePrograms returns all loaded programs`() = runBlockingTest {
        whenever(homeConnectApi.getAvailablePrograms("appliance_id")).thenReturn(HomeConnectApiResponse(AvailableProgramsData(listOf(
                testAvailableProgram,
                testAvailableProgram.copy(ProgramKey.PreHeating),
        ))))

        val result = interactor.getAvailablePrograms(forApplianceId = "appliance_id")

        assertEquals(result, listOf(testAvailableProgram, testAvailableProgram.copy(ProgramKey.PreHeating)))
    }

    @Test
    fun `startProgram delegates to HomeConnectApi`() = runBlockingTest {

        interactor.startProgram(forApplianceId = "appliance_id", testStartProgramRequest)

        verify(homeConnectApi).startProgram(forApplianceId = "appliance_id", HomeConnectApiRequest(testStartProgramRequest))
    }

}