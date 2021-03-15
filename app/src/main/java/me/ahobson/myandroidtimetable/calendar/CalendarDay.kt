package me.ahobson.myandroidtimetable.calendar

import java.util.*

class CalendarDay (private val date: Date,
                   private val classes: Array<CalendarItem>
                   )
{
    fun getDateString(): String {
        var year = date.year
        var day = if (date.day < 10) "0" + date.day.toString() else date.day.toString()
        var month = if (date.month < 10) "0" + date.month.toString() else date.month.toString()

        return "$day/$month/$year"
    }

    fun happensOnDay(year: Int, month: Int, day: Int): Boolean {
        return this.date.year == year && this.date.month == month && this.date.day == day
    }
}