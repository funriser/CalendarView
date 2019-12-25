package com.funrisestudio.calendarview

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.annotation.ColorInt


fun Context.dip(value: Int): Int {
    return value * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.dip(value: Float): Float {
    return value * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.sp(value: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, resources.displayMetrics)
}

fun Context.sp(value: Int): Int {
    return sp(value.toFloat()).toInt()
}

fun View.dip(value: Int): Int {
    return context.dip(value)
}

fun View.dip(value: Float): Float {
    return context.dip(value)
}

fun View.sp(value: Int): Int {
    return context.sp(value)
}

fun View.sp(value: Float): Float {
    return context.sp(value)
}

@ColorInt
fun Context.color(@ColorRes colorId: Int): Int {
    return ContextCompat.getColor(this, colorId)
}

@ColorInt
fun View.color(@ColorRes colorId: Int): Int {
    return context.color(colorId)
}

@ColorInt
fun Context.getAccentColor(): Int {
    val attrId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        android.R.attr.colorAccent
    } else {
        resources.getIdentifier("colorAccent", "attr", packageName)
    }
    val value = TypedValue()
    theme.resolveAttribute(attrId, value, true)
    return value.data
}

@ColorInt
fun View.getAccentColor(): Int {
    return context.getAccentColor()
}