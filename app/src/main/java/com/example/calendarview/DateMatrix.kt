package com.example.calendarview

class DateMatrix {

    fun hasCell(i: Int, j: Int, matrixParams: Params): Boolean {
        val cellNumber = i * DATE_ROW_LEN + j
        val matrixEndNumber = matrixParams.end * DATE_ROW_LEN
        return cellNumber >= matrixParams.start && cellNumber <= matrixEndNumber
    }

    fun getParams(): Params {
        return Params(5, 0, 2)
    }

    class Params(val length: Int, val start: Int, val end: Int)

    companion object {

        const val DATE_ROW_LEN = 7

    }

}