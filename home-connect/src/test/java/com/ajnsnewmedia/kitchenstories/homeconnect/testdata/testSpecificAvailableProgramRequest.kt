package com.ajnsnewmedia.kitchenstories.homeconnect.testdata

const val testResponse_1: String = """
    {
        "key": "Cooking.Oven.Program.HeatingMode.HotAir",
        "name": "4D Heißluft",
        "options": [{
            "name": "Temperatur",
            "key": "Cooking.Oven.Option.SetpointTemperature",
            "constraints": {
                "min": 30,
                "max": 250,
                "default": 160
            },
            "unit": "°C",
            "type": "Double"
        }]
    }
"""
const val testResponse_2: String = """
    {
        "key": "Cooking.Oven.Program.HeatingMode.HotAir",
        "name": "4D Heißluft",
        "options": [{
            "name": "Dauer anpassen",
            "key": "BSH.Common.Option.Duration",
            "constraints": {
                "min": 1,
                "max": 86340,
                "default": 60
            },
            "unit": "seconds",
            "type": "Int"
        }]  
    }
"""

const val testResponse_3: String = """
     {
        "key": "Cooking.Oven.Program.HeatingMode.HotAir",
        "name": "4D Heißluft",
        "options": [{
            "key": "Cooking.Oven.Option.FastPreHeat",
            "type": "Boolean",
            "constraints": {
                "default": false
            },
            "name": "Schnellaufheizen"
        }]  
    }
"""

const val testResponse_4: String = """
    {
        "key": "Cooking.Oven.Program.HeatingMode.HotAir",
        "name": "4D Heißluft",
        "options": [{
             "name": "Startzeit",
             "key": "BSH.Common.Option.StartInRelative",
            "constraints": {
                "min": 0,
                "max": 86340,
                "default": 0
            },
            "unit": "seconds",
            "type": "Int"
        }]  
    }
"""
