package com.example.calendarview

class MonthMatrix(
    val month: Int,
    val year: Int
) {

    val length: Int
        get() = CalendarAPI.getWeeksCount(month, year)

    private val firstInd: Int
        get() = CalendarAPI.getFirstWeekDayOfMonth(month, year) - 1

    private val lastInd: Int
        get() {
            val lastWeekDay = CalendarAPI.getLastWeekDayOfMonth(month, year) - 1
            return DATE_ROW_LEN * (length - 1) + lastWeekDay
        }

    fun hasCell(i: Int, j: Int): Boolean {
        val cellNumber = i * DATE_ROW_LEN + j
        return cellNumber in firstInd..lastInd
    }

    companion object {

        const val DATE_ROW_LEN = 7

    }

}