package com.ajnsnewmedia.kitchenstories.homeconnect.model.programs

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ProgramOptionKeyJsonTest {

    data class TestProgramOptionKeyClass(val key: ProgramOptionKey)

    private lateinit var adapter: JsonAdapter<TestProgramOptionKeyClass>

    private fun testJson(withKey: String) = """
        {
            "key": "$withKey"
        }
    """.filter { !it.isWhitespace() }

    @Before
    fun setUp() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        adapter = moshi.adapter(TestProgramOptionKeyClass::class.java)
    }

    @Test
    fun `reads all supported program keys correctly`() {
        Assert.assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Option.SetpointTemperature"))?.key, ProgramOptionKey.SetpointTemperature
        )
        Assert.assertEquals(
            adapter.fromJson(testJson("BSH.Common.Option.Duration"))?.key, ProgramOptionKey.Duration
        )
        Assert.assertEquals(
            adapter.fromJson(testJson("Cooking.Oven.Option.FastPreHeat"))?.key, ProgramOptionKey.FastPreHeat
        )
        Assert.assertEquals(
            adapter.fromJson(testJson("BSH.Common.Option.StartInRelative"))?.key, ProgramOptionKey.StartInRelative
        )
    }

    @Test
    fun `writes all supported program keys correctly`() {
        Assert.assertEquals(
            adapter.toJson(TestProgramOptionKeyClass(ProgramOptionKey.SetpointTemperature)),
            testJson("Cooking.Oven.Option.SetpointTemperature")
        )
        Assert.assertEquals(
            adapter.toJson(TestProgramOptionKeyClass(ProgramOptionKey.Duration)), testJson("BSH.Common.Option.Duration")
        )
        Assert.assertEquals(
            adapter.toJson(TestProgramOptionKeyClass(ProgramOptionKey.FastPreHeat)), testJson("Cooking.Oven.Option.FastPreHeat")
        )
        Assert.assertEquals(
            adapter.toJson(TestProgramOptionKeyClass(ProgramOptionKey.StartInRelative)), testJson("BSH.Common.Option.StartInRelative")
        )
    }

}