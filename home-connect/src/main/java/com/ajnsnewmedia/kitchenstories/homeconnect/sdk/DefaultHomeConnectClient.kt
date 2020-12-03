package com.ajnsnewmedia.kitchenstories.homeconnect.sdk

import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectClientCredentials
import com.ajnsnewmedia.kitchenstories.homeconnect.util.DefaultErrorHandler
import com.ajnsnewmedia.kitchenstories.homeconnect.util.DefaultHomeConnectApiFactory
import kotlinx.coroutines.Dispatchers

class DefaultHomeConnectClient(
    baseUrl: String,
    clientCredentials: HomeConnectClientCredentials,
    private val homeConnectSecretsStore: HomeConnectSecretsStore,
) : HomeConnectClient by DefaultHomeConnectInteractor(
    DefaultHomeConnectApiFactory(baseUrl, homeConnectSecretsStore).also {
        AuthorizationDependencies.homeConnectApiFactory = it
    },
    homeConnectSecretsStore,
    DefaultErrorHandler(),
    Dispatchers.IO,
) {
    init {
        AuthorizationDependencies.baseUrl = baseUrl
        AuthorizationDependencies.credentials = clientCredentials
        AuthorizationDependencies.homeConnectSecretsStore = homeConnectSecretsStore
    }
}
