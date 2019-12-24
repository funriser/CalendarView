package com.funrisestudio.calendarview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.layout_calendar_view.view.*
import java.util.*

class CalendarView : LinearLayout, MonthAdapter.MonthOwner {

    private var textMonthSize = sp(14f)
    private var textMonthColor = color(android.R.color.black)
    private var textMonthMargin = dip(30)
    private var chevronSize = dip(30)
    private var chevronColor = color(android.R.color.black)
    private var chevronSideMargin = dip(12)

    private val monthAdapter = MonthAdapter(this)
    private var monthViewParams: MonthView.Params
        set(value) {
            field = value
            monthAdapter.invalidatePageParams(value)
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
            setMonthTitle(position)
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

    private fun setMonthTitle(monthData: MonthData) {
        val monthName = CalendarAPI.getMonthName(monthData.month)
        tvMonthTitle.text = monthName
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
                chevronSize = getDimension(R.styleable.CalendarView_chevronSize, chevronSize.toFloat()).toInt()
                chevronSideMargin = getDimension(R.styleable.CalendarView_chevronSideMargin, chevronSideMargin.toFloat()).toInt()
                chevronColor = getColor(R.styleable.CalendarView_chevronColor, chevronColor)
                monthViewParams = MonthView.Params(
                    textColor = getColor(
                        R.styleable.CalendarView_textColor,
                        MonthView.getDefaultTextColor(ctx)
                    ),
                    textColorSelected = getColor(
                        R.styleable.CalendarView_textColorSelected,
                        MonthView.getDefaultTextColorSelected(
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
                    weekDayTitleColor = getColor(
                        R.styleable.CalendarView_textWeekdayColor,
                        MonthView.getDefaultWeekdayTitleColor(
                            ctx
                        )
                    ),
                    textDaySize = getDimension(
                        R.styleable.CalendarView_textDaySize,
                        MonthView.getDefaultTextDaySize(ctx)
                    ),
                    mrgWeekDayTitle = getDimension(
                        R.styleable.CalendarView_marginWeekdayTop,
                        MonthView.getDefaultMarginWeekdayTitle(
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
        chevronLeftLP.width = chevronSize
        chevronLeftLP.height = chevronSize
        chevronLeftLP.marginStart = chevronSideMargin
        ibChevron.layoutParams = chevronLeftLP
        ibChevron.imageTintList = ColorStateList.valueOf(chevronColor)
    }

    override fun onCreateMonthView(context: Context, monthData: MonthData): MonthView {
        return MonthView(context, monthViewParams, monthData)
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
        return parentHeight - tvMonthTitle.measuredHeight - textMonthMargin
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
        monthViewParams = monthViewParams.copy(textColor = color)
    }

    /**
     * Set color for the text that displays day of month when it is selected
     * @param color desired color
     */
    fun setTextColorSelected(@ColorInt color: Int) {
        monthViewParams = monthViewParams.copy(textColorSelected = color)
    }

    /**
     * Set color for the mark that indicates that the day of month is selected
     * Selected date represents date that was selected by user with touch interaction
     * Selected date shows the color indicator
     * @param color desired color
     */
    fun setSelectionColor(@ColorInt color: Int) {
        monthViewParams = monthViewParams.copy(selectionColor = color)
    }

    /**
     * Set color for the mark that indicates that the day of month is highlighted
     * Highlighted date shows the color indicator
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
        monthViewParams = monthViewParams.copy(weekDayTitleColor = color)
    }

    /**
     * Set text size for the text that displays day of month
     * @param textSize desired text size in px
     */
    fun setTextDaySize(textSize: Float) {
        monthViewParams = monthViewParams.copy(textDaySize = textSize)
    }

    /**
     * Set margin between the text that displays week of month and
     * the text that represents days of month
     * @param margin desired margin in px
     */
    fun setMarginWeekday(margin: Int) {
        monthViewParams = monthViewParams.copy(mrgWeekDayTitle = margin)
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
    fun setChevronSize(size: Int) {
        chevronSize = size
        invalidate()
    }

    /**
     * Set side margin of the arrows that switch dates
     * @param margin desired margin in px
     */
    fun setChevronSideMargin(margin: Int) {
        chevronSideMargin = margin
        invalidate()
    }

    /**
     * Set color of the arrows that switch dates
     * @param color desired color
     */
    fun setChevronColor(@ColorInt color: Int) {
        chevronColor = color
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