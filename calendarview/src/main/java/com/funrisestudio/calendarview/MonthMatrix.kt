package com.funrisestudio.calendarview

class MonthMatrix(internal val monthData: MonthData) {

    constructor(month: Int, year: Int) : this(MonthData(month, year))

    val length: Int

    private val firstInd: Int
    private val lastInd: Int

    init {
        length = getMatrixLength()
        firstInd = getMatrixFirstIndex()
        lastInd = getMatrixLastIndex()
    }

    fun hasCell(i: Int, j: Int): Boolean {
        val cellNumber = i * DATE_ROW_LEN + j
        return cellNumber in firstInd..lastInd
    }

    private fun getMatrixFirstIndex(): Int {
        return CalendarAPI.getFirstWeekDayOfMonthIndex(
            monthData.month,
            monthData.year
        )
    }

    private fun getMatrixLastIndex(): Int {
        val lastWeekDay = CalendarAPI.getLastWeekDayOfMonthIndex(
            monthData.month,
            monthData.year
        )
        return DATE_ROW_LEN * (length - 1) + lastWeekDay
    }

    private fun getMatrixLength(): Int {
        return CalendarAPI.getWeeksCount(
            monthData.month,
            monthData.year
        )
    }

    companion object {
        const val DATE_ROW_LEN = CalendarAPI.DAYS_IN_WEEK
        const val MAX_ROWS = 6
    }

}