package com.example.calendarview

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import java.util.*

class MonthAdapter: PagerAdapter() {

    var onDateSelected: ((Date) -> Unit)? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val monthData = getMonthData(position)
        val monthView = MonthView(container.context, monthData).apply {
            onDateSelected = this@MonthAdapter.onDateSelected
        }
        container.addView(monthView)
        return monthView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val monthView = `object` as MonthView
        monthView.onDateSelected = null
        container.removeView(monthView)
    }

    override fun getCount(): Int {
        return Int.MAX_VALUE
    }

    internal fun getMonthData(position: Int): MonthData {
        val calendarMonth = position % 12
        val year = (position / 12) + 1
        return MonthData(calendarMonth, year)
    }

    internal fun getPosition(monthData: MonthData): Int {
        return (monthData.year - 1) * 12 + monthData.month
    }

}