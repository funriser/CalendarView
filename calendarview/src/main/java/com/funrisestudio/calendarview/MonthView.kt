package com.funrisestudio.calendarview

import android.content.Context
import android.graphics.*
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
        internal fun getDefaultMarginWeekdayTop(context: Context): Int {
            return context.dip(15)
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
                marginWeekdayTop = getDefaultMarginWeekdayTop(
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

    private lateinit var paintDateText: Paint
    private lateinit var paintSelectedDateText: Paint
    private lateinit var paintCellSelection: Paint
    private lateinit var paintCellHighlight: Paint
    private lateinit var paintWeekDayTitle: Paint

    private val textRectBuf = Rect()

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
        paintDateText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = params.textDayColor
            this.textSize = params.textDaySize
        }
        paintSelectedDateText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = params.textDayColorSelected
            this.textSize = params.textDaySize
        }
        paintCellSelection = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = params.selectionColor
        }
        paintCellHighlight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = params.highlightColor
        }
        paintWeekDayTitle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = params.textWeekdayColor
            this.textSize = params.textWeekdaySize
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
        val weekDayTextSample =
            CalendarAPI.getWeekDayShortName(0) //get localed week day text sample
        val textRect = Rect()
        measureText(weekDayTextSample, paintWeekDayTitle, textRect)
        return textRect.height() + params.marginWeekdayTop
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

    private fun measureText(text: String, paint: Paint, rect: Rect) {
        paint.getTextBounds(text, 0, text.length, rect)
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
        internal var marginWeekdayTop: Int,
        internal var paddingSelection: Int
    )

}