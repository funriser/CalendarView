package com.funrisestudio.calendarview

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.*

class CalendarTest {

    @Test
    fun `should get first day of month for december 2019`() {
        assertEquals(Calendar.SUNDAY,
            CalendarAPI.getFirstWeekDayOfMonth(
                Calendar.DECEMBER,
                2019
            )
        )
    }

    @Test
    fun `should get first day of month for september 19998`() {
        assertEquals(Calendar.TUESDAY,
            CalendarAPI.getFirstWeekDayOfMonth(
                Calendar.SEPTEMBER,
                1998
            )
        )
    }

    @Test
    fun `should get last day of month for december 2019`() {
        assertEquals(Calendar.TUESDAY,
            CalendarAPI.getLastWeekDayOfMonth(
                Calendar.DECEMBER,
                2019
            )
        )
    }

    @Test
    fun `should get last day of month for september 19998`() {
        assertEquals(Calendar.WEDNESDAY,
            CalendarAPI.getLastWeekDayOfMonth(
                Calendar.SEPTEMBER,
                1998
            )
        )
    }

    @Test
    fun `should return weeks count in december 2019`() {
        assertEquals(5,
            CalendarAPI.getWeeksCount(Calendar.DECEMBER, 2019)
        )
    }

    @Test
    fun `should return weeks count in june 2019`() {
        assertEquals(6,
            CalendarAPI.getWeeksCount(Calendar.JUNE, 2019)
        )
    }

    @Test
    fun `should get month name correctly (february)`() {
        val monthName = CalendarAPI.getMonthName(1)
        assertEquals("February", monthName)
    }

}