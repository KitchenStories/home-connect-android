package com.ajnsnewmedia.kitchenstories.homeconnect.model.programs

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class ProgramKey {
    @Json(name = "Cooking.Oven.Program.HeatingMode.PreHeating") PreHeating,
    @Json(name = "Cooking.Oven.Program.HeatingMode.HotAir") HotAir,
    @Json(name = "Cooking.Oven.Program.HeatingMode.HotAirEco") HotAirEco,
    @Json(name = "Cooking.Oven.Program.HeatingMode.HotAirGrilling") HotAirGrilling,
    @Json(name = "Cooking.Oven.Program.HeatingMode.TopBottomHeating") TopBottomHeating,
    @Json(name = "Cooking.Oven.Program.HeatingMode.TopBottomHeatingEco") TopBottomHeatingEco,
    @Json(name = "Cooking.Oven.Program.HeatingMode.BottomHeating") BottomHeating,
    @Json(name = "Cooking.Oven.Program.HeatingMode.PizzaSetting") PizzaSetting,
    @Json(name = "Cooking.Oven.Program.HeatingMode.SlowCook") SlowCook,
    @Json(name = "Cooking.Oven.Program.HeatingMode.IntensiveHeat") IntensiveHeat,
    @Json(name = "Cooking.Oven.Program.HeatingMode.KeepWarm") KeepWarm,
    @Json(name = "Cooking.Oven.Program.HeatingMode.PreheatOvenware") PreheatOvenware,
    @Json(name = "Cooking.Oven.Program.HeatingMode.FrozenHeatupSpecial") FrozenHeatupSpecial,
    @Json(name = "Cooking.Oven.Program.HeatingMode.Desiccation") Desiccation,
    @Json(name = "Cooking.Oven.Program.HeatingMode.Defrost") Defrost,
    @Json(name = "Cooking.Oven.Program.HeatingMode.Proof") Proof,
    Unknown,
}
