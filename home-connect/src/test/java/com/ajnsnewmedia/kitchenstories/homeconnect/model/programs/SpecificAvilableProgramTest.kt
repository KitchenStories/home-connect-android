package com.ajnsnewmedia.kitchenstories.homeconnect.model.programs

import com.ajnsnewmedia.kitchenstories.homeconnect.model.jsonadapters.HomeConnectMoshiBuilder
import com.ajnsnewmedia.kitchenstories.homeconnect.testdata.testResponse_1
import com.ajnsnewmedia.kitchenstories.homeconnect.testdata.testResponse_2
import com.ajnsnewmedia.kitchenstories.homeconnect.testdata.testResponse_3
import com.ajnsnewmedia.kitchenstories.homeconnect.testdata.testResponse_4
import com.squareup.moshi.JsonAdapter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SpecificAvailableProgramTest {
    private lateinit var adapter: JsonAdapter<SpecificAvailableProgram>

    private var testSpecificAvailableProgarm1 = SpecificAvailableProgram(
        key = "Cooking.Oven.Program.HeatingMode.HotAir",
        name = "4D Heißluft",
        options = listOf(
            ProgramOptions(
                name = "Temperatur",
                key = "Cooking.Oven.Option.SetpointTemperature",
                constraints = ProgramConstraints(
                    min = 30,
                    max = 250,
                    default = 160.0,
                    stepsize = null,
                    allowedvalues = null
                ),
                unit = "°C",
                type = "Double"
            )
        )
    )
    private var testSpecificAvailableProgarm2 = SpecificAvailableProgram(
        key = "Cooking.Oven.Program.HeatingMode.HotAir",
        name = "4D Heißluft",
        options = listOf(
            ProgramOptions(
                name = "Dauer anpassen",
                key = "BSH.Common.Option.Duration",
                constraints = ProgramConstraints(
                    min = 1,
                    max = 86340,
                    default = 60.0,
                    stepsize = null,
                    allowedvalues = null
                ),
                unit = "seconds",
                type = "Int"
            )
        )
    )
    private var testSpecificAvailableProgarm3 = SpecificAvailableProgram(
        key = "Cooking.Oven.Program.HeatingMode.HotAir",
        name = "4D Heißluft",
        options = listOf(
            ProgramOptions(
                name = "Schnellaufheizen",
                key = "Cooking.Oven.Option.FastPreHeat",
                constraints = ProgramConstraints(
                    min = null,
                    max = null,
                    default = false,
                    stepsize = null,
                    allowedvalues = null
                ),
                unit = null,
                type = "Boolean"
            )
        )
    )

    private var testSpecificAvailableProgarm4 = SpecificAvailableProgram(
        key = "Cooking.Oven.Program.HeatingMode.HotAir",
        name = "4D Heißluft",
        options = listOf(
            ProgramOptions(
                name = "Startzeit",
                key = "BSH.Common.Option.StartInRelative",
                constraints = ProgramConstraints(
                    min = 0,
                    max = 86340,
                    default = 0.0,
                    stepsize = null,
                    allowedvalues = null
                ),
                unit = "seconds",
                type = "Int"
            )
        )
    )

    @Before
    fun setUp() {
        val moshi = HomeConnectMoshiBuilder.moshiInstance
        adapter = moshi.adapter(SpecificAvailableProgram::class.java)
    }

    @Test
    fun `test with program option key as SetPointTemperature`() {
        Assert.assertEquals(
            adapter.fromJson(testResponse_1),
            testSpecificAvailableProgarm1
        )
    }

    @Test
    fun `test with program option key as Duration`() {
        Assert.assertEquals(
            adapter.fromJson(testResponse_2),
            testSpecificAvailableProgarm2
        )
    }

    @Test
    fun `test with program option key as FastPreHeat`() {
        Assert.assertEquals(
            adapter.fromJson(testResponse_3),
            testSpecificAvailableProgarm3
        )
    }

    @Test
    fun `test with program option key as StartInRelative `() {
        Assert.assertEquals(
            adapter.fromJson(testResponse_4),
            testSpecificAvailableProgarm4
        )
    }
}