package com.example.calendarview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.layout_calendar_view.view.*

class CalendarView : LinearLayout {

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
        setMonthTitle(0)
    }

    private fun setMonthTitle(position: Int) {
        val monthData = pagerMonth.getMonthAdapter().getMonthData(position)
        val monthName = CalendarAPI.getMonthName(monthData.month)
        tvMonthTitle.text = monthName
    }

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

}