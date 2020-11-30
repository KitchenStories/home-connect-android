package com.ajnsnewmedia.kitchenstories.homeconnect.model.jsonadapters

import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeApplianceType
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.ProgramKey
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.ProgramOptionKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.EnumJsonAdapter

object HomeConnectMoshiBuilder {
    val moshiInstance: Moshi
        get() = Moshi.Builder()
                .add(HomeApplianceType::class.java,
                     EnumJsonAdapter.create(HomeApplianceType::class.java).withUnknownFallback(HomeApplianceType.Unknown))
                .add(ProgramKey::class.java, EnumJsonAdapter.create(ProgramKey::class.java).withUnknownFallback(ProgramKey.Unknown))
                .add(ProgramOptionKey::class.java,
                     EnumJsonAdapter.create(ProgramOptionKey::class.java).withUnknownFallback(ProgramOptionKey.Unknown))
                .build()
}