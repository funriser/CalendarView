package com.funrisestudio.calendarview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.layout_calendar_view.view.*
import java.util.*

class CalendarView : LinearLayout {

    private var textMonthSize = sp(14f)
    private var textMonthColor = color(android.R.color.black)
    private var textMonthMargin = dip(30)
    private var arrowsSize = dip(30)
    private var arrowsColor = color(android.R.color.black)
    private var arrowsSideMargin = dip(12)
    private var monthHeaderGravity = MonthHeaderGravity.CENTER

    private val monthAdapter = MonthAdapter()

    private var monthViewParams: MonthView.Params
        set(value) {
            field = value
            monthAdapter.monthViewParams = value
        }

    var onDateSelected: ((Date) -> Unit)? = null
        set(value) {
            field = value
            monthAdapter.onDateSelected = value
        }

    var highlightedDates: List<Date>? = null
        set(value) {
            field = value
            monthAdapter.highlightedDates = value
        }

    var selectedDate: Date? = null
        get() = monthAdapter.selectedDate
        set(value) {
            field = value
            value?.let {
                monthAdapter.setNewSelectedDate(it)
            }
        }

    var onMonthChanged: ((monthStart: Date) -> Unit)? = null

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
            monthAdapter.currentPosition = position
            val monthData = monthAdapter.getMonthData(position)
            setMonthTitle(monthData)
            onMonthChanged?.invoke(CalendarAPI.getMonthStartDate(monthData))
        }

    }

    init {
        orientation = VERTICAL
        View.inflate(context, R.layout.layout_calendar_view, this)
        pagerMonth.adapter = monthAdapter
        pagerMonth.addOnPageChangeListener(monthChangeListener)
        setMonth(CalendarAPI.getCurrentMonthData())
        ibChevronLeft.setOnClickListener {
            pagerMonth.currentItem --
        }
        ibChevronRight.setOnClickListener {
            pagerMonth.currentItem ++
        }
    }

    private fun setMonth(monthData: MonthData) {
        val positionForMonth = monthAdapter.getPosition(monthData)
        pagerMonth.currentItem = positionForMonth
        setMonthTitle(monthData)
    }

    private fun setMonthTitle(position: Int) {
        val monthData = monthAdapter.getMonthData(position)
        setMonthTitle(monthData)
    }

    @SuppressLint("SetTextI18n")
    private fun setMonthTitle(monthData: MonthData) {
        val monthName = CalendarAPI.getMonthName(monthData.month)
        tvMonthTitle.text = "$monthName ${monthData.year}"
    }

    constructor(ctx: Context) : super(ctx) {
        monthViewParams = MonthView.getDefaultParams(ctx)
        init()
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CalendarView,
            0, 0).apply {
            try {
                textMonthSize = getDimensionPixelSize(R.styleable.CalendarView_textMonthSize, textMonthSize.toInt()).toFloat()
                textMonthColor = getColor(R.styleable.CalendarView_textMonthColor, textMonthColor)
                textMonthMargin = getDimension(R.styleable.CalendarView_textMonthMargin, textMonthMargin.toFloat()).toInt()
                arrowsSize = getDimension(R.styleable.CalendarView_arrowsSize, arrowsSize.toFloat()).toInt()
                arrowsSideMargin = getDimension(R.styleable.CalendarView_arrowsSideMargin, arrowsSideMargin.toFloat()).toInt()
                arrowsColor = getColor(R.styleable.CalendarView_arrowsColor, arrowsColor)
                val monthHeaderGravityValue = getInt(R.styleable.CalendarView_monthHeaderGravity, MonthHeaderGravity.CENTER.ordinal)
                monthHeaderGravity = MonthHeaderGravity.getByOrdinal(monthHeaderGravityValue)
                monthViewParams = MonthView.Params(
                    textDayColor = getColor(
                        R.styleable.CalendarView_textDayColor,
                        MonthView.getDefaultTextDayColor(ctx)
                    ),
                    textDayColorSelected = getColor(
                        R.styleable.CalendarView_textDayColorSelected,
                        MonthView.getDefaultTextDayColorSelected(
                            ctx
                        )
                    ),
                    selectionColor = getColor(
                        R.styleable.CalendarView_selectionColor,
                        MonthView.getDefaultSelectionColor(
                            ctx
                        )
                    ),
                    highlightColor = getColor(
                        R.styleable.CalendarView_highlightColor,
                        MonthView.getDefaultHighlightColor(
                            ctx
                        )
                    ),
                    textWeekdayColor = getColor(
                        R.styleable.CalendarView_textWeekdayColor,
                        MonthView.getDefaultTextWeekdayColor(
                            ctx
                        )
                    ),
                    textDaySize = getDimension(
                        R.styleable.CalendarView_textDaySize,
                        MonthView.getDefaultTextDaySize(ctx)
                    ),
                    marginWeekdayBottom = getDimension(
                        R.styleable.CalendarView_marginWeekdayTop,
                        MonthView.getDefaultMarginWeekdayBottom(
                            ctx
                        ).toFloat()
                    ).toInt(),
                    paddingSelection = getDimension(
                        R.styleable.CalendarView_paddingSelection,
                        MonthView.getDefaultPaddingSelection(
                            ctx
                        ).toFloat()
                    ).toInt(),
                    textWeekdaySize = getDimension(
                        R.styleable.CalendarView_textWeekdaySize,
                        MonthView.getDefaultTextWeekdaySize(
                            ctx
                        )
                    )
                )
            } finally {
                recycle()
            }
        }
        init()
    }

    private fun init() {
        setMonthHeaderParams(monthHeaderGravity)
        tvMonthTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textMonthSize)
        tvMonthTitle.setTextColor(textMonthColor)
        val lpMonthView = pagerMonth.layoutParams as MarginLayoutParams
        lpMonthView.topMargin = textMonthMargin
        pagerMonth.layoutParams = lpMonthView
        setChevronParams(ibChevronLeft)
        setChevronParams(ibChevronRight)
    }

    private fun setChevronParams(ibChevron: ImageButton) {
        val chevronLeftLP = ibChevron.layoutParams as LayoutParams
        chevronLeftLP.width = arrowsSize
        chevronLeftLP.height = arrowsSize
        chevronLeftLP.marginStart = arrowsSideMargin
        ibChevron.layoutParams = chevronLeftLP

        val newDrawable = DrawableCompat.wrap(ibChevron.drawable)
        DrawableCompat.setTint(newDrawable.mutate(), arrowsColor)
    }

    private fun setMonthHeaderParams(monthHeaderGravity: MonthHeaderGravity) {
        val lpMonthHeader = when(monthHeaderGravity) {
            MonthHeaderGravity.CENTER -> return
            MonthHeaderGravity.START -> {
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.START
                }
            }
            MonthHeaderGravity.END -> {
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.END
                }
            }
        }
        layoutMonthHeader.layoutParams = lpMonthHeader
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val pagerMonthLp = pagerMonth.layoutParams
        pagerMonthLp.width = if (isExactMeasureMode(widthMode)) {
            getExactPagerDimension(width)
        } else {
            //need to include margins for at_most measurement
            val pagerMrgWidth = width - marginStart - marginEnd
            if (isExactMeasureMode(heightMode)) {
                //calculate based on height
                getPagerWidth(pagerMrgWidth, height)
            } else {
                val pagerDesiredWidth = getPagerDesiredWidth(width)
                getExactPagerDimension(pagerDesiredWidth)
            }
        }

        val pagerMonthAvailableHeight = getPagerAvailableHeight(width, height)
        pagerMonthLp.height = if (isExactMeasureMode(heightMode)) {
            getExactPagerDimension(pagerMonthAvailableHeight)
        } else {
            //need to include margins for at_most measurement
            val pagerMrgHeight = pagerMonthAvailableHeight - marginTop - marginBottom
            getPagerHeight(pagerMrgHeight, pagerMonthLp.width)
        }
        pagerMonth.layoutParams = pagerMonthLp

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun isExactMeasureMode(mode: Int): Boolean {
        return mode == MeasureSpec.EXACTLY
    }

    private fun getPagerDesiredWidth(fullWidth: Int): Int {
        return (fullWidth * PAGER_WIDTH_PERCENT).toInt()
    }

    /**
     * Takes the text view on top into account
     */
    private fun getPagerAvailableHeight(parentWidth: Int, parentHeight: Int): Int {
        val tvWidthSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.AT_MOST)
        val tvHeightSpec = MeasureSpec.makeMeasureSpec(parentHeight, MeasureSpec.AT_MOST)
        tvMonthTitle.measure(tvWidthSpec, tvHeightSpec)
        //10 is to optimize height measurements with linear layout
        return parentHeight - tvMonthTitle.measuredHeight - textMonthMargin - 10
    }

    private fun getExactPagerDimension(availableDimension: Int): Int {
        return availableDimension
    }

    private fun getPagerWidth(availableWidth: Int, height: Int): Int {
        val desiredWidth = (height / MONTH_PAGER_RATIO).toInt()
        return if (availableWidth >= desiredWidth) {
            desiredWidth
        } else {
            availableWidth
        }
    }

    private fun getPagerHeight(availableHeight: Int, width: Int): Int {
        val desiredHeight = (width * MONTH_PAGER_RATIO).toInt()
        return if (availableHeight >= desiredHeight) {
            desiredHeight
        } else {
            //horizontal layout?
            availableHeight / 3
        }
    }

    /**
     * Set month displayed by calendar
     * @param month month of year from 0 to 11. 0 is for January and 11 is for December
     * @param year year
     */
    fun setMonth(month: Int, year: Int) {
        val monthData = MonthData(month, year)
        setMonth(monthData)
    }

    /**
     * Set text size for the text that displays month name
     * @param textMonthSize desired text size in px
     */
    fun setTextMonthSize(textMonthSize: Float) {
        this.textMonthSize = textMonthSize
        invalidate()
    }

    /**
     * Set text color for the text that displays month name
     * @param textMonthColor desired text color
     */
    fun setTextMonthColor(@ColorInt textMonthColor: Int) {
        this.textMonthColor = textMonthColor
        invalidate()
    }

    /**
     * Set margin between month title and calendar matrix
     * @param textMonthMargin desired margin in px
     */
    fun setTextMonthMargin(textMonthMargin: Int) {
        this.textMonthMargin = textMonthMargin
        invalidate()
    }

    /**
     * Set text color for the text that displays day of month
     * @param color desired color
     */
    fun setTextDayColor(@ColorInt color: Int) {
        monthViewParams = monthViewParams.copy(textDayColor = color)
    }

    /**
     * Set color for the text that displays day of month when it is selected
     * @param color desired color
     */
    fun setTextDayColorSelected(@ColorInt color: Int) {
        monthViewParams = monthViewParams.copy(textDayColorSelected = color)
    }

    /**
     * Set color for the mark that indicates that the day of month is selected
     * Selected day is marked by a primary indicator
     * @param color desired color
     */
    fun setSelectionColor(@ColorInt color: Int) {
        monthViewParams = monthViewParams.copy(selectionColor = color)
    }

    /**
     * Set color for the mark that indicates that the day of month is highlighted
     * Highlighted day is marked by a secondary indicator
     * @param color desired color
     */
    fun setHighlightColor(@ColorInt color: Int) {
        monthViewParams = monthViewParams.copy(highlightColor = color)
    }

    /**
     * Set text color for the text that displays week of month
     * @param color desired color
     */
    fun setTextWeekdayColor(@ColorInt color: Int) {
        monthViewParams = monthViewParams.copy(textWeekdayColor = color)
    }

    /**
     * Set text size for the text that displays day of month
     * @param textSize desired text size in px
     */
    fun setTextDaySize(textSize: Float) {
        monthViewParams = monthViewParams.copy(textDaySize = textSize)
    }

    /**
     * Set margin between the text that displays day of week and
     * the text that represents days of month
     * @param margin desired margin in dp
     */
    fun setMarginWeekdayBottom(margin: Int) {
        monthViewParams = monthViewParams.copy(marginWeekdayBottom = margin)
    }

    /**
     * Padding for the indicators of highlighted and selected dates
     * @param padding desired padding
     */
    fun setPaddingSelection(padding: Int) {
        monthViewParams = monthViewParams.copy(paddingSelection = padding)
    }


    /**
     * Set text size for the text that displays week of month
     * @param textSize desired text size in px
     */
    fun setTextWeekdaySize(textSize: Float) {
        monthViewParams = monthViewParams.copy(textWeekdaySize = textSize)
    }

    /**
     * Set size of the arrows that switch dates
     * @param size desired text size in px
     */
    fun setArrowsSize(size: Int) {
        arrowsSize = size
        invalidate()
    }

    /**
     * Set side margin of the arrows that switch dates
     * @param margin desired margin in px
     */
    fun setArrowsSideMargin(margin: Int) {
        arrowsSideMargin = margin
        invalidate()
    }

    /**
     * Set color of the arrows that switch dates
     * @param color desired color
     */
    fun setArrowsColor(@ColorInt color: Int) {
        arrowsColor = color
        invalidate()
    }

    /**
     * @param gravity desired gravity
     *      MonthHeaderGravity.CENTER - month title and arrows spread in center
     *      MonthHeaderGravity.START - month title and arrows in the start corner
     *      MonthHeaderGravity.END - month title and arrows in the end corner corner
     */
    fun setMonthHeaderGravity(gravity: MonthHeaderGravity) {
        monthHeaderGravity = gravity
        invalidate()
    }

    override fun invalidate() {
        init()
        super.invalidate()
    }

    companion object {

        //height to width ratio
        const val MONTH_PAGER_RATIO = 0.75

        //optimal percent of the parent view width
        const val PAGER_WIDTH_PERCENT = 0.75

    }

}