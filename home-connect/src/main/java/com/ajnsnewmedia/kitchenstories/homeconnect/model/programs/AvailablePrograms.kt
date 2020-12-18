package com.ajnsnewmedia.kitchenstories.homeconnect.model.programs

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AvailableProgramsData(val programs: List<AvailableProgram>)

@JsonClass(generateAdapter = true)
data class AvailableProgram(val key: String, val name: String = key.getFallbackString(), val constraints: ProgramConstraint?) {
}

@JsonClass(generateAdapter = true)
data class ProgramConstraint(val execution: ProgramExecutionConstraint)

@JsonClass(generateAdapter = false)
enum class ProgramExecutionConstraint {
    none,
    selectonly,
    startonly,
    selectandstart,
}

private fun String.getFallbackString(): String {
    return this.split('.').last().camelToSpaceCase()
}

val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

//from https://stackoverflow.com/questions/60010298/how-can-i-convert-a-camel-case-string-to-snake-case-and-back-in-idiomatic-kotlin
fun String.camelToSpaceCase(): String {
    return camelRegex.replace(this) {
        " ${it.value}"
    }
}
