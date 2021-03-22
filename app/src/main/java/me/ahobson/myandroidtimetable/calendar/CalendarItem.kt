package me.ahobson.myandroidtimetable.calendar

import java.io.Serializable

class CalendarItem (val startHour: Int,
                    val startMinute: Int,
                    val durationMinutes: Int,
                    val courseTitle: String,
                    val rawRoom: String,
                    val classType: ClassType): Serializable {
    override fun toString() = "$courseTitle at $startHour"

    val room = rawRoom
}
