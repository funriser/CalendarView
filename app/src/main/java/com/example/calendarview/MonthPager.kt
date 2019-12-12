package com.example.calendarview

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class MonthPager: ViewPager {

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    init {
        adapter = MonthAdapter()
    }

    fun getMonthAdapter(): MonthAdapter {
        return adapter as MonthAdapter
    }

}