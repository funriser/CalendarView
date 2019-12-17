package com.example.calendarview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.layout_calendar_view.view.*
import java.util.*

class CalendarView : LinearLayout {

    var onDateSelected: ((Date) -> Unit)? = null
        set(value) {
            field = value
            pagerMonth.getMonthAdapter().onDateSelected = value
        }

    var highlightedDates: List<Date>? = null
        set(value) {
            field = value
            pagerMonth.getMonthAdapter().highlightedDates = value
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
        pagerMonth.addOnPageChangeListener(monthChangeListener)
        setMonth(CalendarAPI.getCurrentMonthData())
    }

    private fun setMonth(monthData: MonthData) {
        val positionForMonth = pagerMonth.getMonthAdapter().getPosition(monthData)
        pagerMonth.currentItem = positionForMonth
        setMonthTitle(monthData)
    }

    private fun setMonthTitle(position: Int) {
        val monthData = pagerMonth.getMonthAdapter().getMonthData(position)
        setMonthTitle(monthData)
    }

    private fun setMonthTitle(monthData: MonthData) {
        val monthName = CalendarAPI.getMonthName(monthData.month)
        tvMonthTitle.text = monthName
    }

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

}