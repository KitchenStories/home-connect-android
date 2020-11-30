package com.ajnsnewmedia.kitchenstories.homeconnect.model.programs

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProgramKeyJsonTest {

    data class TestProgramKeyClass(val key: ProgramKey)

    private lateinit var adapter: JsonAdapter<TestProgramKeyClass>

    private fun testJson(withKey: String) = """
        {
            "key": "$withKey"
        }
    """.filter { !it.isWhitespace() }

    @Before
    fun setUp() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory())
            .build()
        adapter = moshi.adapter(TestProgramKeyClass::class.java)
    }

    @Test
    fun `reads all supported program keys correctly`() {
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.PreHeating"))?.key,
            ProgramKey.PreHeating
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.HotAir"))?.key,
            ProgramKey.HotAir
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.HotAirEco"))?.key,
            ProgramKey.HotAirEco
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.HotAirGrilling"))?.key,
            ProgramKey.HotAirGrilling
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.TopBottomHeating"))?.key,
            ProgramKey.TopBottomHeating
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.TopBottomHeatingEco"))?.key,
            ProgramKey.TopBottomHeatingEco
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.BottomHeating"))?.key,
            ProgramKey.BottomHeating
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.PizzaSetting"))?.key,
            ProgramKey.PizzaSetting
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.SlowCook"))?.key,
            ProgramKey.SlowCook
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.IntensiveHeat"))?.key,
            ProgramKey.IntensiveHeat
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.KeepWarm"))?.key,
            ProgramKey.KeepWarm
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.PreheatOvenware"))?.key,
            ProgramKey.PreheatOvenware
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.FrozenHeatupSpecial"))?.key,
            ProgramKey.FrozenHeatupSpecial
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.Desiccation"))?.key,
            ProgramKey.Desiccation
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.Defrost"))?.key,
            ProgramKey.Defrost
        )
        assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.Proof"))?.key,
            ProgramKey.Proof
        )
    }

    @Test
    fun `writes all supported program keys correctly`() {
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.PreHeating)),
            testJson("Cooking.Oven.Program.HeatingMode.PreHeating")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.HotAir)),
            testJson("Cooking.Oven.Program.HeatingMode.HotAir")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.HotAirEco)),
            testJson("Cooking.Oven.Program.HeatingMode.HotAirEco")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.HotAirGrilling)),
            testJson("Cooking.Oven.Program.HeatingMode.HotAirGrilling")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.TopBottomHeating)),
            testJson("Cooking.Oven.Program.HeatingMode.TopBottomHeating")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.TopBottomHeatingEco)),
            testJson("Cooking.Oven.Program.HeatingMode.TopBottomHeatingEco")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.BottomHeating)),
            testJson("Cooking.Oven.Program.HeatingMode.BottomHeating")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.PizzaSetting)),
            testJson("Cooking.Oven.Program.HeatingMode.PizzaSetting")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.SlowCook)),
            testJson("Cooking.Oven.Program.HeatingMode.SlowCook")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.IntensiveHeat)),
            testJson("Cooking.Oven.Program.HeatingMode.IntensiveHeat")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.KeepWarm)),
            testJson("Cooking.Oven.Program.HeatingMode.KeepWarm")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.PreheatOvenware)),
            testJson("Cooking.Oven.Program.HeatingMode.PreheatOvenware")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.FrozenHeatupSpecial)),
            testJson("Cooking.Oven.Program.HeatingMode.FrozenHeatupSpecial")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.Desiccation)),
            testJson("Cooking.Oven.Program.HeatingMode.Desiccation")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.Defrost)),
            testJson("Cooking.Oven.Program.HeatingMode.Defrost")
        )
        assertEquals(
            adapter.toJson(TestProgramKeyClass(ProgramKey.Proof)),
            testJson("Cooking.Oven.Program.HeatingMode.Proof")
        )
    }

}