package me.ahobson.myandroidtimetable.calendar

interface CalendarClickListener {

    fun clickedCalendarItem(item: CalendarItem, rawX: Float, rawY: Float)
}
