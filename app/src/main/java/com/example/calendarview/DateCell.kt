package com.example.calendarview

import android.graphics.RectF

data class DateCell (
    val rect: RectF,
    val number: Int,
    var isSelected: Boolean = false,
    var isHighlighted: Boolean = false
) {

    fun isPointInside(x: Int, y: Int): Boolean {
        return (x >= rect.left
                && x <= rect.right
                && y >= rect.top
                && y <= rect.bottom)
    }

    fun isPointInside(x: Float, y: Float): Boolean {
        return (x >= rect.left
                && x <= rect.right
                && y >= rect.top
                && y <= rect.bottom)
    }

}