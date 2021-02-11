package com.ajnsnewmedia.kitchenstories.homeconnect.testdata

private fun testResponse_1(): String = """
    {
    "data": {
    "key": "Cooking.Oven.Program.HeatingMode.HotAir",
    "options": [
    {
        "name": "Temperatur",
        "key": "Cooking.Oven.Option.SetpointTemperature",
        "constraints": {
        "min": 30,
        "max": 250,
        "default": 160
    },
        "unit": "°C",
        "type": "Double"
    },
    {
        "name": "Dauer anpassen",
        "key": "BSH.Common.Option.Duration",
        "constraints": {
        "min": 1,
        "max": 86340,
        "default": 60
    },
        "unit": "seconds",
        "type": "Int"
    },
    {
        "key": "Cooking.Oven.Option.FastPreHeat",
        "type": "Boolean",
        "constraints": {
        "default": false
    },
        "name": "Schnellaufheizen"
    },
    {
        "name": "Startzeit",
        "key": "BSH.Common.Option.StartInRelative",
        "constraints": {
        "min": 0,
        "max": 86340,
        "default": 0
    },
        "unit": "seconds",
        "type": "Int"
    }
    ],
    "name": "4D Heißluft"
}
}
"""
