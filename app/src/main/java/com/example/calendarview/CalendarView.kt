package com.example.calendarview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.layout_calendar_view.view.*
import java.util.*

class CalendarView : LinearLayout, MonthAdapter.MonthOwner {

    private var textMonthSize = 20f
    private var textMonthColor = Color.BLACK
    private var textMonthMargin = 10

    private val monthAdapter = MonthAdapter(this)
    private var monthViewParams: MonthView.Params? = null

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
                    textDaySize = getColor(R.styleable.CalendarView_textDaySize, MonthView.defaultTextDaySize.toInt()).toFloat(),
                    mrgWeekDayTitle = getDimension(R.styleable.CalendarView_marginWeekdayTop, MonthView.defaultMrgWeekDayTitle.toFloat()).toInt(),
                    paddingSelection = getDimension(R.styleable.CalendarView_paddingSelection, MonthView.defaultPaddingSelection.toFloat()).toInt()
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
        return if (monthViewParams == null) {
            MonthView(context, monthData)
        } else {
            MonthView(context, monthViewParams!!, monthData)
        }
    }

}