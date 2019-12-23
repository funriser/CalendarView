package com.example.calendarview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.layout_calendar_view.view.*
import java.util.*

class CalendarView : LinearLayout, MonthAdapter.MonthOwner {

    private var textMonthSize = 20f
    private var textMonthColor = Color.BLACK
    private var textMonthMargin = 10

    private val monthAdapter = MonthAdapter(this)
    private var monthViewParams: MonthView.Params
        set(value) {
            field = value
            monthAdapter.invalidatePageParams(value)
        }

    var onDateSelected: ((Date) -> Unit)? = null
        set(value) {
            field = value
            monthAdapter.onDateSelected = value
        }

    var highlightedDates: List<Date>? = null
        set(value) {
            field = value
            monthAdapter.highlightedDates = value
        }

    private val monthChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            setMonthTitle(position)
        }

    }

    init {
        orientation = VERTICAL
        View.inflate(context, R.layout.layout_calendar_view, this)
        pagerMonth.adapter = monthAdapter
        pagerMonth.addOnPageChangeListener(monthChangeListener)
        setMonth(CalendarAPI.getCurrentMonthData())
    }

    private fun setMonth(monthData: MonthData) {
        val positionForMonth = monthAdapter.getPosition(monthData)
        pagerMonth.currentItem = positionForMonth
        setMonthTitle(monthData)
    }

    private fun setMonthTitle(position: Int) {
        val monthData = monthAdapter.getMonthData(position)
        setMonthTitle(monthData)
    }

    private fun setMonthTitle(monthData: MonthData) {
        val monthName = CalendarAPI.getMonthName(monthData.month)
        tvMonthTitle.text = monthName
    }

    constructor(ctx: Context) : super(ctx) {
        monthViewParams = MonthView.getDefaultParams()
        init()
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CalendarView,
            0, 0).apply {
            try {
                textMonthSize = getDimensionPixelSize(R.styleable.CalendarView_textMonthSize, textMonthSize.toInt()).toFloat()
                textMonthColor = getColor(R.styleable.CalendarView_textMonthColor, textMonthColor)
                textMonthMargin = getDimension(R.styleable.CalendarView_textMonthMargin, textMonthMargin.toFloat()).toInt()
                monthViewParams = MonthView.Params(
                    textColor = getColor(R.styleable.CalendarView_textColor, MonthView.defaultTextColor),
                    textColorSelected = getColor(R.styleable.CalendarView_textColorSelected, MonthView.defaultTextColorSelected),
                    selectionColor = getColor(R.styleable.CalendarView_selectionColor, MonthView.defaultSelectionColor),
                    highlightColor = getColor(R.styleable.CalendarView_highlightColor, MonthView.defaultHighlightColor),
                    weekDayTitleColor = getColor(R.styleable.CalendarView_textWeekdayColor, MonthView.defaultWeekdayTitleColor),
                    textDaySize = getDimension(R.styleable.CalendarView_textDaySize, MonthView.defaultTextDaySize),
                    mrgWeekDayTitle = getDimension(R.styleable.CalendarView_marginWeekdayTop, MonthView.defaultMrgWeekDayTitle.toFloat()).toInt(),
                    paddingSelection = getDimension(R.styleable.CalendarView_paddingSelection, MonthView.defaultPaddingSelection.toFloat()).toInt(),
                    textWeekdaySize = getDimension(R.styleable.CalendarView_textWeekdaySize, MonthView.defaultTextWeekdaySize)
                )
            } finally {
                recycle()
            }
        }
        init()
    }

    private fun init() {
        tvMonthTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textMonthSize)
        tvMonthTitle.setTextColor(textMonthColor)
        val lpMonthView = pagerMonth.layoutParams as MarginLayoutParams
        lpMonthView.topMargin = textMonthMargin
        pagerMonth.layoutParams = lpMonthView
    }

    override fun onCreateMonthView(context: Context, monthData: MonthData): MonthView {
        return MonthView(context, monthViewParams, monthData)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val pagerMonthLp = pagerMonth.layoutParams
        pagerMonthLp.width = if (isExactMeasureMode(widthMode)) {
            getExactPagerDimension(width)
        } else {
            //need to include margins for at_most measurement
            val pagerMrgWidth = width - marginStart - marginEnd
            if (isExactMeasureMode(heightMode)) {
                //calculate based on height
                getPagerWidth(pagerMrgWidth, height)
            } else {
                getExactPagerDimension(pagerMrgWidth)
            }
        }

        val pagerMonthAvailableHeight = getPagerAvailableHeight(width, height)
        pagerMonthLp.height = if (isExactMeasureMode(heightMode)) {
            getExactPagerDimension(pagerMonthAvailableHeight)
        } else {
            //need to include margins for at_most measurement
            val pagerMrgHeight = pagerMonthAvailableHeight - marginTop - marginBottom
            getPagerHeight(pagerMrgHeight, pagerMonthLp.width)
        }
        pagerMonth.layoutParams = pagerMonthLp

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun isExactMeasureMode(mode: Int): Boolean {
        return mode == MeasureSpec.EXACTLY
    }

    /**
     * Takes the text view on top into account
     */
    private fun getPagerAvailableHeight(parentWidth: Int, parentHeight: Int): Int {
        val tvWidthSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.AT_MOST)
        val tvHeightSpec = MeasureSpec.makeMeasureSpec(parentHeight, MeasureSpec.AT_MOST)
        tvMonthTitle.measure(tvWidthSpec, tvHeightSpec)
        return parentHeight - tvMonthTitle.measuredHeight - textMonthMargin
    }

    private fun getExactPagerDimension(availableDimension: Int): Int {
        return availableDimension
    }

    private fun getPagerWidth(availableWidth: Int, height: Int): Int {
        val desiredWidth = (height / MONTH_PAGER_RATIO).toInt()
        return if (availableWidth >= desiredWidth) {
            desiredWidth
        } else {
            availableWidth
        }
    }

    private fun getPagerHeight(availableHeight: Int, width: Int): Int {
        val desiredHeight = (width * MONTH_PAGER_RATIO).toInt()
        return if (availableHeight >= desiredHeight) {
            desiredHeight
        } else {
            //horizontal layout?
            availableHeight / 3
        }
    }

    fun setTextMonthSize(textMonthSize: Float) {
        this.textMonthSize = textMonthSize
        invalidate()
    }

    fun setTextMonthColor(textMonthColor: Int) {
        this.textMonthColor = textMonthColor
        invalidate()
    }

    fun setTextMonthMargin(textMonthMargin: Int) {
        this.textMonthMargin = textMonthMargin
        invalidate()
    }

    fun setTextColor(color: Int) {
        monthViewParams = monthViewParams.copy(textColor = color)
    }

    fun setTextColorSelected(color: Int) {
        monthViewParams = monthViewParams.copy(textColorSelected = color)
    }

    fun setSelectionColor(color: Int) {
        monthViewParams = monthViewParams.copy(selectionColor = color)
    }

    fun setHighlightColor(color: Int) {
        monthViewParams = monthViewParams.copy(highlightColor = color)
    }

    fun setTextWeekdayColor(color: Int) {
        monthViewParams = monthViewParams.copy(weekDayTitleColor = color)
    }

    fun setTextDaySize(textSize: Float) {
        monthViewParams = monthViewParams.copy(textDaySize = textSize)
    }

    fun setMarginWeekdayTop(margin: Int) {
        monthViewParams = monthViewParams.copy(mrgWeekDayTitle = margin)
    }

    fun setPaddingSelection(padding: Int) {
        monthViewParams = monthViewParams.copy(paddingSelection = padding)
    }

    fun setTextWeekdaySize(textSize: Float) {
        monthViewParams = monthViewParams.copy(textWeekdaySize = textSize)
    }

    override fun invalidate() {
        init()
        super.invalidate()
    }

    companion object {

        //height to width ratio
        const val MONTH_PAGER_RATIO = 0.6

    }

}