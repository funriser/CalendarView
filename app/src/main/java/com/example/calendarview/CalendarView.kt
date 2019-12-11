package com.example.calendarview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*

class CalendarView: View {

    private val dateMatrix = MonthMatrix(Calendar.JULY,2019)
    private var currentCells: List<List<DateCell?>> = emptyList()

    private val textColor = Color.BLACK

    private val paintDateText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = 40f
    }

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        currentCells = getCurrentCells(w, h)
    }

    private val textRect = Rect()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        currentCells.forEach { cellRow ->
            cellRow.forEach cell@ {  dateCell ->
                dateCell?:return@cell

                val dateText = dateCell.number.toString()
                paintDateText.getTextBounds(dateText, 0, dateText.length, textRect)

                val centerX = dateCell.rect.left + dateCell.rect.width() / 2
                val centerY = dateCell.rect.top + dateCell.rect.height() / 2

                val textX = centerX - textRect.width() / 2 - textRect.left
                val textY = centerY - textRect.height() / 2 - textRect.bottom

                canvas.drawText(dateCell.number.toString(), textX, textY, paintDateText)
            }
        }
    }

    private fun getCurrentCells(width: Int, height: Int): List<List<DateCell?>> {
        var dateNumber = 1
        var currX = 0f
        var currY = 0f
        val cellWidth = width.toFloat() / MonthMatrix.DATE_ROW_LEN
        val cellHeight = height.toFloat() / dateMatrix.length

        return List(dateMatrix.length) { i ->
            val dateRow = List(7) cellBuilder@ { j: Int ->
                if (!dateMatrix.hasCell(i, j)) {
                    currX += cellWidth
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
            currX = 0f
            currY += cellHeight
            return@List dateRow
        }
    }

}