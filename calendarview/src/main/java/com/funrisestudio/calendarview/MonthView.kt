package com.funrisestudio.calendarview

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*
import kotlin.math.min

class MonthView: View {

    companion object {

        internal fun getDefaultTextDayColor(context: Context): Int {
            return context.color(android.R.color.black)
        }
        internal fun getDefaultTextDayColorSelected(context: Context): Int {
            return context.color(android.R.color.white)
        }
        internal fun getDefaultSelectionColor(context: Context): Int {
            return context.getAccentColor()
        }
        internal fun getDefaultHighlightColor(context: Context): Int {
            return context.color(R.color.colorGreyHighlight)
        }
        internal fun getDefaultTextWeekdayColor(context: Context): Int {
            return context.color(R.color.colorWeekdayText)
        }
        internal fun getDefaultMarginWeekdayBottom(context: Context): Int {
            return context.dip(5)
        }
        internal fun getDefaultTextDaySize(context: Context): Float {
            return context.sp(12f)
        }
        internal fun getDefaultPaddingSelection(context: Context): Int {
            return context.dip(5)
        }
        internal fun getDefaultTextWeekdaySize(context: Context): Float {
            return context.sp(12f)
        }

        internal fun getDefaultParams(context: Context): Params {
            return Params(
                textDayColor = getDefaultTextDayColor(
                    context
                ),
                textDayColorSelected = getDefaultTextDayColorSelected(
                    context
                ),
                selectionColor = getDefaultSelectionColor(
                    context
                ),
                highlightColor = getDefaultHighlightColor(
                    context
                ),
                textWeekdayColor = getDefaultTextWeekdayColor(
                    context
                ),
                textDaySize = getDefaultTextDaySize(
                    context
                ),
                marginWeekdayBottom = getDefaultMarginWeekdayBottom(
                    context
                ),
                paddingSelection = getDefaultPaddingSelection(
                    context
                ),
                textWeekdaySize = getDefaultTextWeekdaySize(
                    context
                )
            )
        }

    }

    internal var dateMatrix = MonthMatrix(Calendar.JULY, 2019)
    private var currentCells: List<List<DateCell?>> = emptyList()
    private var weekDayTitleCells: List<WeekDayCell> = emptyList()

    private var params: Params
        set(value) {
            field = value
            init()
        }

    internal var selectedDate: Date? = null
        set(value) {
            field = value
            if (value == null) {
                selectedDateCell?.isSelected = false
                selectedDateCell = null
                return
            }
            forEachCell {
                it?:return@forEachCell
                if (isMonthDaySelectedSelected(it.number, value)) {
                    setDateCellSelected(it)
                }
            }
        }

    private var selectedDateCell: DateCell? = null
        set(value) {
            field = value
            invalidate()
        }

    internal var highLightedDates: List<Date>? = null
        set(value) {
            field = value
            if (currentCells.isNotEmpty() && value != null) {
                applyHighlightedDates(value)
            }
            invalidate()
        }

    internal var onDateSelected: ((Date) -> Unit)? = null

    private lateinit var paintDateText: TextPaint
    private lateinit var paintSelectedDateText: TextPaint
    private lateinit var paintCellSelection: Paint
    private lateinit var paintCellHighlight: Paint
    private lateinit var paintWeekDayTitle: TextPaint

    constructor(ctx: Context) : super(ctx) {
        params = getDefaultParams(ctx)
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        params = getDefaultParams(ctx)
    }

    constructor(ctx: Context, params: Params, monthData: MonthData): super(ctx) {
        dateMatrix = MonthMatrix(monthData)
        this.params = params
    }

    private fun init() {
        paintDateText = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = params.textDayColor
            this.textSize = params.textDaySize
            this.textAlign = Paint.Align.CENTER
        }
        paintSelectedDateText = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = params.textDayColorSelected
            this.textSize = params.textDaySize
            this.textAlign = Paint.Align.CENTER
        }
        paintCellSelection = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = params.selectionColor
        }
        paintCellHighlight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = params.highlightColor
        }
        paintWeekDayTitle = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = params.textWeekdayColor
            this.textSize = params.textWeekdaySize
            this.textAlign = Paint.Align.CENTER
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val weekDayTitleHeight = getWeekDayCellHeight()
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

    private fun getWeekDayCellHeight(): Int {
        val textHeight = (paintWeekDayTitle.descent() - paintWeekDayTitle.ascent()).toInt()
        return textHeight + params.marginWeekdayBottom
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
                    drawCircleInsideRectangle(canvas, dateCell.rect, paintCellHighlight, params.paddingSelection)
                    drawTextInsideRect(canvas, dateCell.number.toString(), dateCell.rect, paintSelectedDateText)
                }
            } else {
                //if selected
                drawCircleInsideRectangle(canvas, dateCell.rect, paintCellSelection, params.paddingSelection)
                drawTextInsideRect(canvas, dateCell.number.toString(), dateCell.rect, paintSelectedDateText)
            }
        }
    }

    private fun drawTextInsideRect(
        c: Canvas, text: String,
        rect: RectF, paint: Paint
    ) {
        val tHeight = paint.descent() - paint.ascent()
        val tOffset = (tHeight / 2) - paint.descent()
        c.drawText(text, rect.centerX(), rect.centerY() + tOffset, paint)
    }

    private fun drawTextInsideRectTopAlign(
        c: Canvas, text: String,
        rect: RectF, paint: TextPaint
    ) {
        val decorOffset = 1
        val baselineY = rect.top - paint.ascent() - paint.descent() + decorOffset
        c.drawText(text, rect.centerX(), baselineY, paint)
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
        setDateCellSelected(dateCell)
        val selectedDate = CalendarAPI.getDate(
            dateMatrix.monthData,
            dateCell.number
        )
        onDateSelected?.invoke(selectedDate)
        invalidate()
    }

    private fun setDateCellSelected(dateCell: DateCell) {
        if (dateCell == selectedDateCell) {
            return
        }
        dateCell.isSelected = true
        selectedDateCell?.isSelected = false
        selectedDateCell = dateCell
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
        val cellHeight = height.toFloat() / MonthMatrix.MAX_ROWS

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
                val isSelected = if (selectedDate == null) {
                    false
                } else {
                    isMonthDaySelectedSelected(dateNumber, selectedDate!!)
                }
                val newCell = DateCell(cellRect, dateNumber, isSelected)
                if (isSelected) {
                    setDateCellSelected(newCell)
                }
                dateNumber ++
                currX += cellWidth
                return@cellBuilder newCell
            }
            currX = 0f
            currY += cellHeight
            return@List dateRow
        }
    }

    private fun getWeekDaysTitleCells(width: Int, cellHeight: Int): List<WeekDayCell> {
        val cellWidth = width.toFloat() / MonthMatrix.DATE_ROW_LEN
        var currX = 0f
        val t = 0f
        val b = t + cellHeight
        return List(MonthMatrix.DATE_ROW_LEN) {
            val l = currX
            val r = currX + cellWidth
            currX += cellWidth
            val weekDayTitle = CalendarAPI.getWeekDayShortName(it)
            val weekDayTitleRect = RectF(l, t, r, b)
            return@List WeekDayCell(weekDayTitle, weekDayTitleRect)
        }
    }

    private fun applyHighlightedDates(dates: List<Date>) {
        forEachCell { dateCell ->
            dateCell?:return@forEachCell
            dates.forEach {
                val dayOfMonth = DayOfMonthData(
                    dateMatrix.monthData,
                    dateCell.number
                )
                if (CalendarAPI.isDateMatching(it, dayOfMonth)) {
                    dateCell.isHighlighted = true
                }
            }
        }
        invalidate()
    }

    private fun isMonthDaySelectedSelected(monthDayNumber: Int, selectedDate: Date): Boolean {
        val monthData = dateMatrix.monthData
        val dayOfMonthData = DayOfMonthData(monthData, monthDayNumber)
        return CalendarAPI.isDateMatching(selectedDate, dayOfMonthData)
    }

    fun setMonthParams(params: Params) {
        this.params = params
        invalidate()
    }

    data class Params(
        internal var textDaySize: Float,
        internal var textDayColor: Int,
        internal var textDayColorSelected: Int,
        internal var selectionColor: Int,
        internal var highlightColor: Int,
        internal var textWeekdaySize: Float,
        internal var textWeekdayColor: Int,
        internal var marginWeekdayBottom: Int,
        internal var paddingSelection: Int
    )

}