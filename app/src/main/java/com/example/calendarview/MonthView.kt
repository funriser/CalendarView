package com.example.calendarview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*
import kotlin.math.min

class MonthView: View {

    private var dateMatrix = MonthMatrix(Calendar.JULY,2019)
    private var currentCells: List<List<DateCell?>> = emptyList()

    private val textColor = Color.BLACK
    private val textColorSelected = Color.WHITE
    private val selectionColor = Color.GREEN
    private val highlightColor = Color.GRAY

    private var selectedDate: DateCell? = null

    private val paintDateText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = 40f
    }

    private val paintSelectedDateText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColorSelected
        textSize = 40f
    }

    private val paintCellSelection = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = selectionColor
    }

    private val paintCellHighlight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = highlightColor
    }

    internal var onDateSelected: ((Date) -> Unit)? = null
    internal var highLightedDates: List<Date>? = null
        set(value) {
            field = value
            if (currentCells.isNotEmpty() && value != null) {
                applyHighlightedDates(value)
            }
        }

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    constructor(ctx: Context, monthData: MonthData): super(ctx) {
        dateMatrix = MonthMatrix(monthData)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        currentCells = getCurrentCells(w, h)
        highLightedDates?.let {
            applyHighlightedDates(it)
        }
    }

    private val textRect = Rect()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        forEachCell { dateCell ->
            dateCell?:return@forEachCell
            if (!dateCell.isSelected) {
                if (!dateCell.isHighlighted) {
                    drawTextInsideRect(canvas, dateCell.number.toString(), dateCell.rect, paintDateText)
                } else {
                    //if highlighted
                    drawCircleInsideRectangle(canvas, dateCell.rect, paintCellHighlight)
                    drawTextInsideRect(canvas, dateCell.number.toString(), dateCell.rect, paintSelectedDateText)
                }
            } else {
                //if selected
                drawCircleInsideRectangle(canvas, dateCell.rect, paintCellSelection)
                drawTextInsideRect(canvas, dateCell.number.toString(), dateCell.rect, paintSelectedDateText)
            }
        }
    }

    private fun drawTextInsideRect(
        c: Canvas, text: String,
        rect: RectF, paint: Paint
    ) {
        paint.getTextBounds(text, 0, text.length, textRect)

        val centerX = rect.left + rect.width() / 2
        val centerY = rect.top + rect.height() / 2

        val textX = centerX - textRect.width() / 2 - textRect.left
        val textY = centerY + textRect.height() / 2 - textRect.bottom

        c.drawText(text, textX, textY, paint)
    }

    private fun drawCircleInsideRectangle(c: Canvas, rect: RectF, paint: Paint) {
        val cx = (rect.left + rect.right) / 2 //average
        val cy = (rect.top + rect.bottom) / 2 //average
        val radius = min(rect.width(), rect.height()) / 2
        c.drawCircle(cx, cy, radius, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?:return super.onTouchEvent(event)
        when(event.action) {
            MotionEvent.ACTION_UP -> {
                if (currentCells.isEmpty()) {
                    return false
                }
                forEachCell { dateCell ->
                    dateCell?:return@forEachCell
                    if (dateCell.isPointInside(event.x, event.y)) {
                        onCellTouched(dateCell)
                        return true
                    }
                }
                return false
            }
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            else -> {
                return super.onTouchEvent(event)
            }
        }
    }

    private fun onCellTouched(dateCell: DateCell) {
        dateCell.isSelected = true
        selectedDate?.isSelected = false
        selectedDate = dateCell
        val selectedDate = CalendarAPI.getDate(dateMatrix.monthData, dateCell.number)
        onDateSelected?.invoke(selectedDate)
        invalidate()
    }

    private inline fun forEachCell(action: (DateCell?) -> Unit) {
        currentCells.forEach { cellRow ->
            cellRow.forEach cell@ {  dateCell ->
                action.invoke(dateCell)
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

    private fun applyHighlightedDates(dates: List<Date>) {
        forEachCell { dateCell ->
            dateCell?:return@forEachCell
            dates.forEach {
                val dayOfMonth = DayOfMonthData(dateMatrix.monthData, dateCell.number)
                if (CalendarAPI.isDateMatching(it, dayOfMonth)) {
                    dateCell.isHighlighted = true
                }
            }
        }
        invalidate()
    }

}