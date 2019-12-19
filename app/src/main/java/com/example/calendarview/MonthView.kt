package com.example.calendarview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*
import kotlin.math.min

class MonthView: View {

    companion object {

        internal const val defaultTextColor = Color.BLACK
        internal const val defaultTextColorSelected = Color.WHITE
        internal const val defaultSelectionColor = Color.GREEN
        internal const val defaultHighlightColor = Color.GRAY
        internal const val defaultWeekdayTitleColor = Color.GREEN
        internal const val defaultMrgWeekDayTitle = 15
        internal const val defaultTextDaySize = 40f
        internal const val defaultPaddingSelection = 10

    }

    private var dateMatrix = MonthMatrix(Calendar.JULY,2019)
    private var currentCells: List<List<DateCell?>> = emptyList()
    private var weekDayTitleCells: List<WeekDayCell> = emptyList()

    private var textColor = defaultTextColor
    private var textColorSelected = defaultTextColorSelected
    private var selectionColor = defaultSelectionColor
    private var highlightColor = defaultHighlightColor
    private var weekDayTitleColor = defaultWeekdayTitleColor

    private var mrgWeekDayTitle = defaultMrgWeekDayTitle
    private var paddingSelection = defaultPaddingSelection

    private var textDaySize = defaultTextDaySize

    private var selectedDate: DateCell? = null

    private lateinit var paintDateText: Paint
    private lateinit var paintSelectedDateText: Paint
    private lateinit var paintCellSelection: Paint
    private lateinit var paintCellHighlight: Paint
    private lateinit var paintWeekDayTitle: Paint

    private val textRectBuf = Rect()

    internal var onDateSelected: ((Date) -> Unit)? = null
    internal var highLightedDates: List<Date>? = null
        set(value) {
            field = value
            if (currentCells.isNotEmpty() && value != null) {
                applyHighlightedDates(value)
            }
        }

    constructor(ctx: Context) : super(ctx) {
        init()
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        init()
    }

    constructor(ctx: Context, monthData: MonthData): super(ctx) {
        dateMatrix = MonthMatrix(monthData)
        init()
    }

    constructor(ctx: Context, params: Params, monthData: MonthData): super(ctx) {
        dateMatrix = MonthMatrix(monthData)
        params.let {
            textColor = it.textColor
            textColorSelected = it.textColorSelected
            selectionColor = it.selectionColor
            highlightColor = it.highlightColor
            weekDayTitleColor = it.weekDayTitleColor
            mrgWeekDayTitle = it.mrgWeekDayTitle
            textDaySize = it.textDaySize
            paddingSelection = it.paddingSelection
        }
        init()
    }

    private fun init() {
        paintDateText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = textColor
            this.textSize = textDaySize
        }
        paintSelectedDateText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = textColorSelected
            this.textSize = textDaySize
        }
        paintCellSelection = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = selectionColor
        }
        paintCellHighlight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = highlightColor
        }
        paintWeekDayTitle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = weekDayTitleColor
            this.textSize = 35f
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val weekDayTitleHeight = getWeekDateTitlesHeight()
        weekDayTitleCells = getWeekDaysTitleCells(w, weekDayTitleHeight)
        currentCells = getCurrentCells(
            startX = 0f,
            startY = weekDayTitleHeight.toFloat(),
            width = w,
            height = h - weekDayTitleHeight
        )
        highLightedDates?.let {
            applyHighlightedDates(it)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        weekDayTitleCells.forEach {
            drawTextInsideRectTopAlign(canvas, it.name, it.rect, paintWeekDayTitle)
        }
        forEachCell { dateCell ->
            dateCell?:return@forEachCell
            if (!dateCell.isSelected) {
                if (!dateCell.isHighlighted) {
                    drawTextInsideRect(canvas, dateCell.number.toString(), dateCell.rect, paintDateText)
                } else {
                    //if highlighted
                    drawCircleInsideRectangle(canvas, dateCell.rect, paintCellHighlight, paddingSelection)
                    drawTextInsideRect(canvas, dateCell.number.toString(), dateCell.rect, paintSelectedDateText)
                }
            } else {
                //if selected
                drawCircleInsideRectangle(canvas, dateCell.rect, paintCellSelection, paddingSelection)
                drawTextInsideRect(canvas, dateCell.number.toString(), dateCell.rect, paintSelectedDateText)
            }
        }
    }

    private fun drawTextInsideRect(
        c: Canvas, text: String,
        rect: RectF, paint: Paint
    ) {
        measureText(text, paint, textRectBuf)

        val centerX = rect.left + rect.width() / 2
        val centerY = rect.top + rect.height() / 2

        val textX = centerX - textRectBuf.width() / 2 - textRectBuf.left
        val textY = centerY + textRectBuf.height() / 2 - textRectBuf.bottom

        c.drawText(text, textX, textY, paint)
    }

    private fun drawTextInsideRectTopAlign(
        c: Canvas, text: String,
        rect: RectF, paint: Paint
    ) {
        measureText(text, paint, textRectBuf)

        val centerX = rect.left + rect.width() / 2

        val textX = centerX - textRectBuf.width() / 2 - textRectBuf.left
        val textY = rect.top + textRectBuf.height() - textRectBuf.bottom

        c.drawText(text, textX, textY, paint)
    }

    private fun drawCircleInsideRectangle(c: Canvas, rect: RectF, paint: Paint, padding: Int = 0) {
        val cx = (rect.left + rect.right) / 2 //average
        val cy = (rect.top + rect.bottom) / 2 //average
        val radius = (min(rect.width(), rect.height()) / 2) - padding
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

    private fun getCurrentCells(
        startX: Float, startY: Float,
        width: Int, height: Int
    ): List<List<DateCell?>> {
        var dateNumber = 1
        var currX = startX
        var currY = startY
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

    private fun getWeekDaysTitleCells(width: Int, textHeight: Int): List<WeekDayCell> {
        val cellWidth = width.toFloat() / MonthMatrix.DATE_ROW_LEN
        var currX = 0f
        val t = 0f
        val b = t + textHeight
        return List(MonthMatrix.DATE_ROW_LEN) {
            val l = currX
            val r = currX + cellWidth
            currX += cellWidth
            val weekDayTitle = CalendarAPI.getWeekDayShortName(it)
            val weekDayTitleRect = RectF(l, t, r, b)
            return@List WeekDayCell(weekDayTitle, weekDayTitleRect)
        }
    }

    private fun getWeekDateTitlesHeight(): Int {
        val weekDayTextSample = CalendarAPI.getWeekDayShortName(0) //get localed week day text sample
        val textRect = Rect()
        measureText(weekDayTextSample, paintWeekDayTitle, textRect)
        return textRect.height() + mrgWeekDayTitle
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

    private fun measureText(text: String, paint: Paint, rect: Rect) {
        paint.getTextBounds(text, 0, text.length, rect)
    }

    class Params(
        internal val textColor: Int,
        internal val textColorSelected: Int,
        internal val selectionColor: Int,
        internal val highlightColor: Int,
        internal val weekDayTitleColor: Int,
        internal var mrgWeekDayTitle: Int,
        internal var textDaySize: Float,
        internal var paddingSelection: Int
    )

}