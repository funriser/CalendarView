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

    fun getMonthName(month: Int): String {
        return Calendar.getInstance().run {
            clearTime()
            set(Calendar.MONTH, month)
            getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())?:""
        }
    }

    fun getCurrentMonthData(): MonthData {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        return MonthData(month, year)
    }

    fun getDate(monthData: MonthData, day: Int): Date {
        return getCalendar(monthData).apply {
            set(Calendar.DAY_OF_MONTH, day)
        }.time
    }

    fun isDateMatching(date: Date, dayOfMonthData: DayOfMonthData): Boolean {
        val calendar1 = Calendar.getInstance().apply {
            time = date
        }
        val calendar2 = Calendar.getInstance().apply {
            clearTime()
            set(Calendar.DAY_OF_MONTH, dayOfMonthData.dayNumber)
            set(Calendar.MONTH, dayOfMonthData.monthData.month)
            set(Calendar.YEAR, dayOfMonthData.monthData.year)
        }
        return calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
    }

    fun filterByMonth(dates: List<Date>, monthData: MonthData): List<Date> {
        return dates.filter {
            isMonthMatching(it, monthData)
        }
    }

    private fun isMonthMatching(date: Date, monthData: MonthData): Boolean {
        val calendar1 = Calendar.getInstance().apply {
            time = date
        }
        val calendar2 = Calendar.getInstance().apply {
            clearTime()
            set(Calendar.MONTH, monthData.month)
            set(Calendar.YEAR, monthData.year)
        }
        return calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
    }

    fun getCalendar(monthData: MonthData): Calendar {
        return Calendar.getInstance().apply {
            clearTime()
            set(Calendar.MONTH, monthData.month)
            set(Calendar.YEAR, monthData.year)
        }
    }

    fun getDate(monthData: MonthData): Date {
        return getCalendar(monthData).time
    }

}

fun Calendar.clearTime() {
    clear(Calendar.HOUR)
    clear(Calendar.MINUTE)
    clear(Calendar.SECOND)
    clear(Calendar.MILLISECOND)
}