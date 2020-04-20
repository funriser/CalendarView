package com.funrisestudio.calendarview

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.*

class CalendarTest {

    @Test
    fun `should get first index of month for december 2019 in en`() {
        Locale.setDefault(Locale.ENGLISH)
        assertEquals(0,
            CalendarAPI.getFirstWeekDayOfMonthIndex(
                Calendar.DECEMBER,
                2019
            )
        )
    }

    @Test
    fun `should get first index of month for december 2019 in rus`() {
        Locale.setDefault(Locale("ru", "RU"))
        assertEquals(6,
            CalendarAPI.getFirstWeekDayOfMonthIndex(
                Calendar.DECEMBER,
                2019
            )
        )
    }

    @Test
    fun `should get first index of month for september 19998 in en`() {
        Locale.setDefault(Locale.ENGLISH)
        assertEquals(2,
            CalendarAPI.getFirstWeekDayOfMonthIndex(
                Calendar.SEPTEMBER,
                1998
            )
        )
    }

    @Test
    fun `should get first index of month for september 19998 in rus`() {
        Locale.setDefault(Locale("ru", "RU"))
        assertEquals(1,
            CalendarAPI.getFirstWeekDayOfMonthIndex(
                Calendar.SEPTEMBER,
                1998
            )
        )
    }

    @Test
    fun `should get last index of month for december 2019 in en`() {
        Locale.setDefault(Locale.ENGLISH)
        assertEquals(2,
            CalendarAPI.getLastWeekDayOfMonthIndex(
                Calendar.DECEMBER,
                2019
            )
        )
    }

    @Test
    fun `should get last index of month for december 2019 in rus`() {
        Locale.setDefault(Locale("ru", "RU"))
        assertEquals(1,
            CalendarAPI.getLastWeekDayOfMonthIndex(
                Calendar.DECEMBER,
                2019
            )
        )
    }

    @Test
    fun `should get last index of month for september 19998 in en`() {
        Locale.setDefault(Locale.ENGLISH)
        assertEquals(3,
            CalendarAPI.getLastWeekDayOfMonthIndex(
                Calendar.SEPTEMBER,
                1998
            )
        )
    }

    @Test
    fun `should get last index of month for september 19998 in rus`() {
        Locale.setDefault(Locale("ru", "RU"))
        assertEquals(2,
            CalendarAPI.getLastWeekDayOfMonthIndex(
                Calendar.SEPTEMBER,
                1998
            )
        )
    }

    @Test
    fun `should return weeks count in december 2019`() {
        Locale.setDefault(Locale.ENGLISH)
        assertEquals(5,
            CalendarAPI.getWeeksCount(Calendar.DECEMBER, 2019)
        )
    }

    @Test
    fun `should return weeks count in june 2019`() {
        Locale.setDefault(Locale.ENGLISH)
        assertEquals(6,
            CalendarAPI.getWeeksCount(Calendar.JUNE, 2019)
        )
    }

    @Test
    fun `should get month name correctly (february) in en`() {
        Locale.setDefault(Locale.ENGLISH)
        val monthName = CalendarAPI.getMonthName(1)
        assertEquals("February", monthName)
    }

    @Test
    fun `should get month name correctly (february) in ru`() {
        Locale.setDefault(Locale("ru", "RU"))
        val monthName = CalendarAPI.getMonthName(1)
        assertEquals("Февраль", monthName)
    }

    @Test
    fun `should get week count in may correctly in en`() {
        Locale.setDefault(Locale.ENGLISH)
        val weekCount = CalendarAPI.getWeeksCount(4, 2020)
        assertEquals(6, weekCount)
    }

    @Test
    fun `should get week count correctly in may for rus locale`() {
        Locale.setDefault(Locale("ru", "RU"))
        val weekCount = CalendarAPI.getWeeksCount(4, 2020)
        assertEquals(5, weekCount)
    }

    @Test
    fun `should get week count correctly in april for rus locale`() {
        Locale.setDefault(Locale("ru", "RU"))
        val weekCount = CalendarAPI.getWeeksCount(3, 2020)
        assertEquals(5, weekCount)
    }

}