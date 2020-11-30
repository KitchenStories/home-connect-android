package com.ajnsnewmedia.kitchenstories.homeconnect

import org.junit.Assert.assertFalse

fun verifyThrowing(action: () -> Unit, verifyError: (error: Throwable) -> Unit) {
    try {
        action()
        throw TestThrowingInternalException()
    } catch (e: Throwable) {
        assertFalse("action did not throw an exception", e is TestThrowingInternalException)
        verifyError(e)
    }
}

private class TestThrowingInternalException : Exception()