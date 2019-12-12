package com.example.calendarview

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class MonthAdapter: PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val monthData = getMonthData(position)
        val monthView = MonthView(container.context, monthData)
        container.addView(monthView)
        return monthView
    }

    internal fun getMonthData(position: Int): MonthData {
        val calendarMonth = position % 12
        return MonthData(calendarMonth, 2020)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return 12
    }

}