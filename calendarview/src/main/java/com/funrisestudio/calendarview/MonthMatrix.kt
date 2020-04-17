package com.funrisestudio.calendarview

class MonthMatrix(internal val monthData: MonthData) {

    constructor(month: Int, year: Int) : this(MonthData(month, year))

    val length: Int
        get() = CalendarAPI.getWeeksCount(
            monthData.month,
            monthData.year
        )

    private val firstInd: Int
        get() = CalendarAPI.getFirstWeekDayOfMonth(
            monthData.month,
            monthData.year
        ) - 1

    private val lastInd: Int
        get() {
            val lastWeekDay = CalendarAPI.getLastWeekDayOfMonth(
                monthData.month,
                monthData.year
            ) - 1
            return DATE_ROW_LEN * (length - 1) + lastWeekDay
        }

    fun hasCell(i: Int, j: Int): Boolean {
        val cellNumber = i * DATE_ROW_LEN + j
        return cellNumber in firstInd..lastInd
    }

    companion object {
        const val DATE_ROW_LEN = 7
        const val MAX_ROWS = 6
    }

}