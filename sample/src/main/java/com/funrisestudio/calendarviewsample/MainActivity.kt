package com.funrisestudio.calendarviewsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val date1 = getDate(1, 2, 2020)
    private val date2 = getDate(1, 3, 2020)
    private val date3 = getDate(2, 3, 2020)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vCalendar.onDateSelected = {
            val sdf = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
            tvSelectedDate.text = sdf.format(it)
        }
        vCalendar.highlightedDates = listOf(date1, date2, date3)
    }

    private fun getDate(day: Int, month: Int, year: Int): Date {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }.time
    }

}