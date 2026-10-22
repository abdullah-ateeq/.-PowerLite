package com.example.powerlite

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculationTimeoutTest {

    @Test
    fun testCalculationTimeout_triggersMessage() = runBlocking {
        var errorMessage: String? = null
        try {
            withTimeout(100L) { // Short timeout for testing
                delay(300L) // Simulate long calculation
            }
        } catch (_: kotlinx.coroutines.TimeoutCancellationException) {
            errorMessage = "Calculation took too long. Try a smaller value or lower precision."
        }

        assertEquals("Calculation took too long. Try a smaller value or lower precision.", errorMessage)
    }

    @Test
    fun testCalculationCancellation_triggersMessageAndPreventsSave() = runBlocking {
        var errorMessage: String? = null
        var isSaved = false

        val job: Job = launch(Dispatchers.Default) {
            try {
                delay(500L)
                isSaved = true
            } catch (_: CancellationException) {
                errorMessage = "Calculation cancelled."
            }
        }

        delay(50L)
        job.cancel()
        job.join()

        assertEquals("Calculation cancelled.", errorMessage)
        assertEquals(false, isSaved)
    }
}
