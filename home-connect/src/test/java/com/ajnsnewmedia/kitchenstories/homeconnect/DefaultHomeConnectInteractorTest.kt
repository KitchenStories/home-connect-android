package com.ajnsnewmedia.kitchenstories.homeconnect

import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectSecretsStore
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectClientCredentials
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.DefaultHomeConnectInteractor
import com.ajnsnewmedia.kitchenstories.homeconnect.util.ErrorHandler
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectApiFactory
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before

import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DefaultHomeConnectInteractorTest {

    @Mock
    private lateinit var homeConnectApi: HomeConnectApi

    @Mock
    private lateinit var homeConnectApiFactory: HomeConnectApiFactory

    @Mock
    private lateinit var homeConnectSecretsStore: HomeConnectSecretsStore

    @Mock
    private lateinit var errorHandler: ErrorHandler

    private lateinit var interactor: DefaultHomeConnectInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        whenever(homeConnectApiFactory.getHomeConnectApi()).thenReturn(homeConnectApi)

        interactor = DefaultHomeConnectInteractor(homeConnectApiFactory, homeConnectSecretsStore, errorHandler)
    }

}