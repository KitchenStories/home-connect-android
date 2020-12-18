package com.ajnsnewmedia.kitchenstories.homeconnect.model.programs

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
class ProgramKey {
    companion object{
         val PreHeating = "Cooking.Oven.Program.HeatingMode.PreHeating";
         val HotAir = "Cooking.Oven.Program.HeatingMode.HotAir";
         val HotAirEco = "Cooking.Oven.Program.HeatingMode.HotAirEco";
         val HotAirGrilling = "Cooking.Oven.Program.HeatingMode.HotAirGrilling";
         val TopBottomHeating = "Cooking.Oven.Program.HeatingMode.TopBottomHeating";
         val TopBottomHeatingEco = "Cooking.Oven.Program.HeatingMode.TopBottomHeatingEco";
         val BottomHeating = "Cooking.Oven.Program.HeatingMode.BottomHeating";
         val PizzaSetting = "Cooking.Oven.Program.HeatingMode.PizzaSetting";
         val SlowCook = "Cooking.Oven.Program.HeatingMode.SlowCook";
         val IntensiveHeat = "Cooking.Oven.Program.HeatingMode.IntensiveHeat";
         val KeepWarm = "Cooking.Oven.Program.HeatingMode.KeepWarm";
         val PreheatOvenware = "Cooking.Oven.Program.HeatingMode.PreheatOvenware";
         val FrozenHeatupSpecial = "Cooking.Oven.Program.HeatingMode.FrozenHeatupSpecial";
         val Desiccation = "Cooking.Oven.Program.HeatingMode.Desiccation";
         val Defrost = "Cooking.Oven.Program.HeatingMode.Defrost";
         val Proof = "Cooking.Oven.Program.HeatingMode.Proof";
    }
}
