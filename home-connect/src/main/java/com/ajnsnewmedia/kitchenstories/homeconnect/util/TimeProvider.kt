package com.ajnsnewmedia.kitchenstories.homeconnect.util

import java.util.*

typealias Milliseconds = Long

interface TimeProvider {

    val currentTimestamp: Milliseconds

}

class DefaultTimeProvider : TimeProvider {

    // TODO optimally, we would Java 8 LocalDate implementation here to respect time zone differences
    override val currentTimestamp: Milliseconds
        get() = Date().time

}