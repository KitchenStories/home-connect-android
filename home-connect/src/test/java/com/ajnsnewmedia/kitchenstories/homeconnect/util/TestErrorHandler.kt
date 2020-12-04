package com.ajnsnewmedia.kitchenstories.homeconnect.util

class TestErrorHandler : ErrorHandler {

    override fun handle(error: Throwable): Nothing {
        throw error
    }

}