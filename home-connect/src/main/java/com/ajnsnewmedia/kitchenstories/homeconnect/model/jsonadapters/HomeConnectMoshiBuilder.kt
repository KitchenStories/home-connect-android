package com.ajnsnewmedia.kitchenstories.homeconnect.model.jsonadapters

import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeApplianceType
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.EnumJsonAdapter

object HomeConnectMoshiBuilder {
    val moshiInstance: Moshi
        get() = Moshi.Builder()
                .add(HomeApplianceType::class.java,
                     EnumJsonAdapter.create(HomeApplianceType::class.java).withUnknownFallback(HomeApplianceType.unknown))
                .build()
}