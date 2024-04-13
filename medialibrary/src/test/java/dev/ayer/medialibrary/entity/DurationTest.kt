package dev.ayer.medialibrary.entity

import org.junit.Test

import org.junit.Assert.*

class DurationTest {

    @Test
    fun assertDurationFromMillisecondsValue() {
        val duration = Duration.fromMilliseconds(200000L)
        assertEquals(3.33f, duration.toMinutes(), 0.01f)
        assertEquals(200L, duration.toSeconds())
        assertEquals(200000L, duration.toMilliseconds())
    }

    @Test
    fun assertDurationFromSecondsValue() {
        val duration = Duration.fromSeconds(200L)
        assertEquals(3.33f, duration.toMinutes(), 0.01f)
        assertEquals(200L, duration.toSeconds())
        assertEquals(200000L, duration.toMilliseconds())
    }

    @Test
    fun assertDurationFromMinutesValue() {
        val duration = Duration.fromMinutes(3L)
        assertEquals(3f, duration.toMinutes(), 0.01f)
        assertEquals(180L, duration.toSeconds())
        assertEquals(180_000L, duration.toMilliseconds())
    }
}
