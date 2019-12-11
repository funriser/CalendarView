package com.example.calendarview

import java.util.*

object CalendarAPI {

    fun getFirstWeekDayOfMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance().apply {
            clearTime()
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    fun getLastWeekDayOfMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance().apply {
            clearTime()
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    fun getWeeksCount(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance().apply {
            clearTime()
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }
        return calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
    }

}

fun Calendar.clearTime() {
    clear(Calendar.HOUR)
    clear(Calendar.MINUTE)
    clear(Calendar.SECOND)
    clear(Calendar.MILLISECOND)
}