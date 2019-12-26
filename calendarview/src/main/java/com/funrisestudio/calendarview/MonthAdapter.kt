package com.funrisestudio.calendarview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import java.util.*

class MonthAdapter: PagerAdapter() {

    internal var currentPosition = -1

    internal var onDateSelected: ((Date) -> Unit)? = null
        set(value) {
            field = value
            monthViews.forEach {
                it.value.onDateSelected = value
            }
        }

    internal var monthViewParams: MonthView.Params? = null
        set(value) {
            field = value
            value?:return
            monthViews.forEach {
                it.value.setMonthParams(value)
            }
        }

    internal var highlightedDates: List<Date>? = null
        set(value) {
            field = value
            monthViews.forEach {
                val monthView = it.value
                val monthData = monthView.dateMatrix.monthData
                setMonthHighlightedDates(monthView, monthData)
            }
        }

    internal var selectedDate: Date? = null
        set(value) {
            field?.let {
                //TODO("Remove redundant month data calculation")
                //it would be better to pass it in the month view callback
                val selectedMonthData = CalendarAPI.getMonthData(it)
                val selectedPosition = getPosition(selectedMonthData)
                if (selectedPosition != currentPosition) {
                    monthViews[selectedPosition]?.selectedDate = null
                }
            }
            field = value
        }

    private val monthViews = hashMapOf<Int, MonthView>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val monthData = getMonthData(position)
        val monthView = getMonthView(container.context, monthData)
        bindMonthView(monthView, monthData)
        monthViews[position] = monthView
        container.addView(monthView)
        return monthView
    }

    private fun getMonthView(context: Context, monthData: MonthData): MonthView {
        return MonthView(context, monthViewParams!!, monthData)
    }

    private fun bindMonthView(monthView: MonthView, monthData: MonthData) {
        setMonthHighlightedDates(monthView, monthData)
        selectedDate?.let {
            setMonthSelectedDate(monthView, monthData, it)
        }
        monthView.onDateSelected = this@MonthAdapter.onDateSelected
    }

    private fun setMonthSelectedDate(monthView: MonthView, monthData: MonthData, selectedDate: Date) {
        if (CalendarAPI.isMonthMatching(selectedDate, monthData)) {
            monthView.selectedDate = selectedDate
        }
    }

    private fun setMonthHighlightedDates(monthView: MonthView, monthData: MonthData) {
        highlightedDates?.let {
            val filteredDates =
                CalendarAPI.filterByMonth(it, monthData)
            if (filteredDates.isNotEmpty()) {
                monthView.highLightedDates = filteredDates
            }
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

    internal fun setNewSelectedDate(date: Date) {
        selectedDate = date
        monthViews.forEach {
            val monthView = it.value
            val monthData = monthView.dateMatrix.monthData
            setMonthSelectedDate(monthView, monthData, date)
        }
    }

}