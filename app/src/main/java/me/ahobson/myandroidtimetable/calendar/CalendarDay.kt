package me.ahobson.myandroidtimetable.calendar

import java.io.Serializable

class CalendarDay (private val year: Int, private val month: Int, private val day: Int,
                   val classes: MutableList<CalendarItem>
                   ): Serializable
{
    fun getDateString(): String {
        val year = year
        val day = if ((day + 1) < 10) "0" + (day + 1).toString() else (day + 1).toString()
        val month = if ((month + 1) < 10) "0" + (month + 1).toString() else (month + 1).toString()

        return "$day/$month/$year"
    }

    fun happensOnDay(year: Int, month: Int, day: Int): Boolean {
        return this.year == year && this.month == month && this.day == day
    }
}