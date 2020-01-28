package com.funrisestudio.calendarview

enum class MonthHeaderGravity {
    CENTER, START, END;

    companion object  {

        internal fun getByOrdinal(ordinal: Int): MonthHeaderGravity {
            return values().find { it.ordinal == ordinal }?:CENTER
        }

    }

}