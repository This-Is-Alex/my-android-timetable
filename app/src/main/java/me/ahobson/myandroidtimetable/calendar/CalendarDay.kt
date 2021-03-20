package me.ahobson.myandroidtimetable.calendar

import android.util.Log
import java.util.*

class CalendarDay (private val year: Int, private val month: Int, private val day: Int,
                   val classes: Array<CalendarItem>
                   )
{
    fun getDateString(): String {
        var year = year
        var day = if ((day + 1) < 10) "0" + (day + 1).toString() else (day + 1).toString()
        var month = if ((month + 1) < 10) "0" + (month + 1).toString() else (month + 1).toString()

        return "$day/$month/$year"
    }

    fun happensOnDay(year: Int, month: Int, day: Int): Boolean {
        return this.year == year && (this.month -1 ) == month && this.day == day
    }
}