package kz.hashiroii.feature.reminders

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderTimeTest {

    @Test
    fun `midnight displays as 12 AM`() {
        assertEquals("12:00 AM", ReminderTime(1, hour = 0, minute = 0).displayTime)
    }

    @Test
    fun `noon displays as 12 PM`() {
        assertEquals("12:00 PM", ReminderTime(1, hour = 12, minute = 0).displayTime)
    }

    @Test
    fun `morning hour formats correctly`() {
        assertEquals("7:30 AM", ReminderTime(1, hour = 7, minute = 30).displayTime)
    }

    @Test
    fun `afternoon hour converts to 12-hour`() {
        assertEquals("6:00 PM", ReminderTime(1, hour = 18, minute = 0).displayTime)
    }

    @Test
    fun `minutes below 10 are zero-padded`() {
        assertEquals("9:05 AM", ReminderTime(1, hour = 9, minute = 5).displayTime)
    }

    @Test
    fun `11 PM displays correctly`() {
        assertEquals("11:59 PM", ReminderTime(1, hour = 23, minute = 59).displayTime)
    }

    @Test
    fun `ALL_DAYS contains all 7 days`() {
        assertEquals(7, ReminderTime.ALL_DAYS.size)
        assertTrue(ReminderTime.ALL_DAYS.containsAll((0..6).toSet()))
    }

    @Test
    fun `default days is ALL_DAYS`() {
        val reminder = ReminderTime(id = 1, hour = 8, minute = 0)
        assertEquals(ReminderTime.ALL_DAYS, reminder.days)
    }

    @Test
    fun `copy with subset of days preserves other fields`() {
        val original = ReminderTime(id = 42, hour = 9, minute = 15, days = ReminderTime.ALL_DAYS)
        val updated = original.copy(days = setOf(0, 4)) // Mon and Fri only
        assertEquals(42, updated.id)
        assertEquals(9, updated.hour)
        assertEquals(15, updated.minute)
        assertEquals(setOf(0, 4), updated.days)
    }
}