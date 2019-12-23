package com.example.calendarview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import java.util.*

class MonthAdapter(private val monthOwner: MonthOwner): PagerAdapter() {

    var onDateSelected: ((Date) -> Unit)? = null

    var highlightedDates: List<Date>? = null

    private val monthViews = hashMapOf<Int, MonthView>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val monthData = getMonthData(position)
        val monthView = monthOwner.onCreateMonthView(container.context, monthData)
        bindMonthView(monthView, monthData)
        monthViews[position] = monthView
        container.addView(monthView)
        return monthView
    }

    private fun bindMonthView(monthView: MonthView, monthData: MonthData) {
        monthView.apply {
            highlightedDates?.let {
                val filteredDates = CalendarAPI.filterByMonth(it, monthData)
                if (filteredDates.isNotEmpty()) {
                    this.highLightedDates = filteredDates
                }
            }
            onDateSelected = this@MonthAdapter.onDateSelected
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val monthView = `object` as MonthView
        monthView.onDateSelected = null
        monthViews.remove(position)
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

    internal fun invalidatePageParams(monthViewParams: MonthView.Params) {
        monthViews.forEach {
            it.value.invalidateParams(monthViewParams)
        }
    }

    interface MonthOwner {

        fun onCreateMonthView(context: Context, monthData: MonthData): MonthView

    }

}