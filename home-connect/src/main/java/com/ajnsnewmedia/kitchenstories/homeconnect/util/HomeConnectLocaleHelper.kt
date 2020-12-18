package com.ajnsnewmedia.kitchenstories.homeconnect.util

class HomeConnectLocaleHelper {
    companion object {

        fun homeConnectLocaleWithFallback(localeString: String, fallback: String): String {
            return if (localeString.toLowerCase() in supportedLocales){
                localeString
            } else{
                fallback
            }
        }
        //List from https://developer.home-connect.com/docs/general/languages
        val supportedLocales = setOf(
                "bg",
                "zh",
                "hr",
                "cs",
                "da",
                "nl",
                "en",
                "fi",
                "fr",
                "de",
                "el",
                "hu",
                "it",
                "nb",
                "pl",
                "pt",
                "ro",
                "ru",
                "sr",
                "sk",
                "sl",
                "es",
                "sv",
                "tr",
                "uk",
                "bg-bg",
                "zh-cn",
                "zh-hk",
                "zh-tw",
                "hr-hr",
                "cs-cz",
                "da-dk",
                "nl-be",
                "nl-nl",
                "en-au",
                "en-ca",
                "en-gb",
                "en-in",
                "en-nz",
                "en-sg",
                "en-us",
                "en-za",
                "fi-fi",
                "fr-be",
                "fr-ca",
                "fr-ch",
                "fr-fr",
                "fr-lu",
                "de-at",
                "de-ch",
                "de-de",
                "de-lu",
                "el-gr",
                "hu-hu",
                "it-ch",
                "it-it",
                "nb-no",
                "pl-pl",
                "pt-pt",
                "ro-ro",
                "ru-ru",
                "sr-sr",
                "sk-sk",
                "sl-si",
                "es-cl",
                "es-es",
                "es-pe",
                "sv-se",
                "tr-tr",
        )
    }
}