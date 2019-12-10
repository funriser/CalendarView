package com.example.calendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CalendarView: View {

    private val dateMatrix = DateMatrix()
    var currentCells: List<List<DateCell?>> = emptyList()

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        currentCells = getCurrentCells(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        currentCells.forEach {

        }
    }

    private fun getCurrentCells(width: Int, height: Int): List<List<DateCell?>> {
        val matrixParams = dateMatrix.getParams()
        val matrixLen = matrixParams.length

        var dateNumber = 1
        var currX = 0f
        var currY = 0f
        val cellWidth = width.toFloat() / DateMatrix.DATE_ROW_LEN
        val cellHeight = height.toFloat() / matrixLen

        return List(matrixLen) { i ->
            val dateRow = List(7) cellBuilder@ { j: Int ->
                if (!dateMatrix.hasCell(i, j, matrixParams)) {
                    return@cellBuilder null
                }
                val l = currX
                val r = currX + cellWidth
                val t = currY
                val b = currY + cellHeight
                val cellRect = RectF(l, t, r, b)
                val newCell = DateCell(cellRect, dateNumber)
                dateNumber ++
                currX += cellWidth
                return@cellBuilder newCell
            }
            currY += cellHeight
            return@List dateRow
        }
    }

}