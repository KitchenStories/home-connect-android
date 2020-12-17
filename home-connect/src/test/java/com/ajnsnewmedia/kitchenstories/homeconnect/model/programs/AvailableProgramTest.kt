package com.ajnsnewmedia.kitchenstories.homeconnect.model.programs

import com.ajnsnewmedia.kitchenstories.homeconnect.model.jsonadapters.HomeConnectMoshiBuilder
import com.squareup.moshi.JsonAdapter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AvailableProgramTest {

    private lateinit var adapter: JsonAdapter<AvailableProgram>

    @Before
    fun setUp() {
        val moshi = HomeConnectMoshiBuilder.moshiInstance
        adapter = moshi.adapter(AvailableProgram::class.java)
    }


    private fun testJson(withKey: String) = """
        {
            "key": "$withKey"
        }
    """.filter { !it.isWhitespace() }


    private fun testJsonWithName(withKey: String, withName: String ) = """
        {
            "key": "$withKey",
            "name": "$withName"
        }
    """.filter { !it.isWhitespace() }

    @Test
    fun `test without name`(){
        Assert.assertEquals(adapter.fromJson(testJson("Cooking.Oven.Program.HeatingMode.PreHeating"))?.name, "Pre Heating")
    }

    @Test
    fun `test when there is no dot in the key name`(){
        Assert.assertEquals(adapter.fromJson(testJson("PreHeating"))?.name, "Pre Heating")
    }

    @Test
    fun `test when there is nothing to convert`(){
        Assert.assertEquals(adapter.fromJson(testJson("Pre Heating"))?.name, "Pre Heating")
    }

    @Test
    fun `test with name`(){
        Assert.assertEquals(adapter.fromJson(testJsonWithName("Cooking.Oven.Program.HeatingMode.PreHeating", "something"))?.name, "something")
    }
}