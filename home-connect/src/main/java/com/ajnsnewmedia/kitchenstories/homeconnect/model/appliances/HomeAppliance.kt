package com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeAppliancesData(val homeappliances: List<HomeAppliance>)

@JsonClass(generateAdapter = true)
data class HomeAppliance(
        @Json(name = "haId") val id: String,
        val name: String,
        val type: HomeApplianceType,
        /* Brand of the home appliance, e.g. \"BOSCH\" */
        val brand: String,
        /* The type code (VIB) of the home appliance. */
        val vib: String,
        /* Combination of VIB and customer index (VIB/KI) */
        val enumber: String,
        /* Current connection state of the home appliance. True if the home appliance is online, false otherwise. */
        val connected: Boolean,
)