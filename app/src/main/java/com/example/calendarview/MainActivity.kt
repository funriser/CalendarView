package com.example.calendarview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vCalendar.onDateSelected = {
            tvSelectedDate.text = formatDate(it)
        }
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).format(date)
    }

}
