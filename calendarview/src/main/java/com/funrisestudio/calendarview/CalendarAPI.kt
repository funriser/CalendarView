package com.funrisestudio.calendarview

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object CalendarAPI {

    private const val FORMAT_MONTH_STANDALONE = "LLLL"
    private const val FORMAT_WEEK_DAY_SHORT = "E"
    const val DAYS_IN_WEEK = 7

    private val current: Calendar
        get() {
            return Calendar.getInstance().apply {
                isLenient = false
            }
        }

    fun getFirstWeekDayOfMonthIndex(month: Int, year: Int): Int {
        val calendar = current.apply {
            clearTime()
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }
        return getDayOfWeekIndex(calendar)
    }

    fun getLastWeekDayOfMonthIndex(month: Int, year: Int): Int {
        val calendar = current.apply {
            clearTime()
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            //to avoid incorrect month calculation after getting day from default instance
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }
        return getDayOfWeekIndex(calendar)
    }

    private fun getDayOfWeekIndex(calendar: Calendar): Int {
        val diff = calendar.get(Calendar.DAY_OF_WEEK) - calendar.firstDayOfWeek
        return if (diff < 0) {
            return DAYS_IN_WEEK + diff
        } else {
            diff
        }
    }

    private fun getDayOfWeekFromIndex(calendar: Calendar, i: Int): Int {
        val pos = i + calendar.firstDayOfWeek
        return if (pos <= DAYS_IN_WEEK) {
            pos
        } else {
            pos - DAYS_IN_WEEK
        }
    }

    fun getWeeksCount(month: Int, year: Int): Int {
        val calendar = current.apply {
            clearTime()
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }
        return if (calendar.get(Calendar.WEEK_OF_MONTH) != 0) {
            calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
        } else {
            calendar.getActualMaximum(Calendar.WEEK_OF_MONTH) + 1
        }
    }

    @SuppressLint("DefaultLocale")
    fun getMonthName(month: Int): String {
        val date = current.apply {
            clearTime()
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MONTH, month)
        }.time
        val sdf = SimpleDateFormat(FORMAT_MONTH_STANDALONE, Locale.getDefault())
        val rowMonthName = sdf.format(date)
        return rowMonthName.capitalize()
    }

    //takes params from 0 to 6
    fun getWeekDayShortName(weekDay: Int): String {
        val date = current.apply {
            clearTime()
            val dayOfWeek = getDayOfWeekFromIndex(this, weekDay)
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
        }.time
        return SimpleDateFormat(FORMAT_WEEK_DAY_SHORT, Locale.getDefault()).format(date)
    }

    fun getCurrentMonthData(): MonthData {
        val calendar = current
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
        val calendar1 = current.apply {
            time = date
        }
        val calendar2 = current.apply {
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

    fun isMonthMatching(date: Date, monthData: MonthData): Boolean {
        val calendar1 = current.apply {
            time = date
        }
        val calendar2 = current.apply {
            clearTime()
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MONTH, monthData.month)
            set(Calendar.YEAR, monthData.year)
        }
        return calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
    }

    private fun getCalendar(monthData: MonthData): Calendar {
        return current.apply {
            clearTime()
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MONTH, monthData.month)
            set(Calendar.YEAR, monthData.year)
        }
    }

    fun getMonthData(date: Date): MonthData {
        val calendar = current.apply {
            time = date
        }
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        return MonthData(month, year)
    }

    fun getMonthStartDate(monthData: MonthData): Date {
        return getCalendar(monthData).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }.time
    }

}

fun Calendar.clearTime() {
    clear(Calendar.HOUR)
    clear(Calendar.MINUTE)
    clear(Calendar.SECOND)
    clear(Calendar.MILLISECOND)
}