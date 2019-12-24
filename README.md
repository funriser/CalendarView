# CalendarView
[![](https://jitpack.io/v/funriser/CalendarView.svg)](https://jitpack.io/#funriser/CalendarView)

CalendarView is a simple and customizable calendar widget with possibilities to select and highlight dates.

![alt text](https://raw.githubusercontent.com/funriser/CalendarView/master/screenshots/screenshot1.png)

![alt text](https://raw.githubusercontent.com/funriser/CalendarView/master/screenshots/screenshot2.png)

## Gradle
To get a CalendarView library into your build:

Step 1. Add in your top-level build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency to your module-level build.gradle:
```gradle
dependencies {
    implementation 'com.github.funriser:CalendarView:v0.1.0'
}
```
## How to use?
### Just add calendar view to your xml layout
```xml
<com.funrisestudio.calendarview.CalendarView
        android:id="@+id/vCalendar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"/>
```
### ...and control it from your Kotlin or Java code
Set callback to get selected date:
```kotlin
vCalendar.onDateSelected = {
    val sdf = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
    tvSelectedDate.text = sdf.format(it)
}
```
Set highlighted dates to indicate them with the special mark:
```kotlin
vCalendar.highlightedDates = listOf(date1, date2, date3)
```
## Customization
### XML
* Month header size: ```app:textMonthSize="[dimension]"```
* Month header margin: ```app:textMonthMargin="[dimension]"```
* Month header color: ```app:textMonthColor="[color]"```
* Arrows size: ```app:arrowsSize="[dimension]"```
* Arrows color: ```app:arrowsColor="[color]"```
* Arrows side margin: ```app:arrowsSideMargin="[dimension]"```
* Day of month text size: ```app:textDaySize="[dimension]"```
* Day of month text color: ```app:textDayColor="[color]"```
* Day of month selected text color: ```app:textDayColorSelected="[color]"```
* Selection indicator color: ```app:selectionColor="[color]"```
* Highlight indicator color: ```app:highlightColor="[color]"```
* Selection indicator padding: ```app:paddingSelection="[dimension]"```
* Day of week text size: ```app:textWeekdaySize="[dimension]"```
* Day of week text color: ```app:textWeekdayColor="[color]"```
* Day of week margin: ```app:marginWeekdayTop="[dimension]"```
### Kotlin
```kotlin
vCalendar.apply {
    setMonth(month, year) //set month to show on calendar
    setHighlightedDates(dates) // Set dates to highlight
    setTextMonthSize(textSize) //Month header size
    setTextMonthColor(color) //Month header color
    setTextMonthMargin(margin) //Month header margin
    setArrowsSize(size) //Arrows size
    setArrowsColor(color) //Arrows color
    setArrowsSideMargin(margin) //Arrows side margin
    setTextDaySize(textSize) //Day of month text size
    setTextDayColor(color) //Day of month text color
    setTextDayColorSelected(color) //Day of month selected text color
    setSelectionColor(color) //Selection indicator color
    setHighlightColor(color) //Highlight indicator color
    setPaddingSelection(padding) //Selection indicator padding
    setTextWeekdaySize(textSize) //Day of week text size
    setTextWeekdayColor(color) //Day of week text color
    setMarginWeekdayTop(margin) //Day of week margin
}
```

