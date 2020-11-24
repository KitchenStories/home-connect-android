package com.ajnsnewmedia.kitchenstories.homeconnect.sdk

import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectAccessToken

interface HomeConnectSecretsStore {

    var accessToken: HomeConnectAccessToken?

}
