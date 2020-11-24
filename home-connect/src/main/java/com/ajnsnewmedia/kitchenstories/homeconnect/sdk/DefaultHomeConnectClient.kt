package com.ajnsnewmedia.kitchenstories.homeconnect.sdk

import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectClientCredentials
import com.ajnsnewmedia.kitchenstories.homeconnect.util.DefaultErrorHandler
import com.ajnsnewmedia.kitchenstories.homeconnect.util.DefaultHomeConnectApiFactory

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
) {
    init {
        AuthorizationDependencies.baseUrl = baseUrl
        AuthorizationDependencies.credentials = clientCredentials
        AuthorizationDependencies.homeConnectSecretsStore = homeConnectSecretsStore
    }
}
