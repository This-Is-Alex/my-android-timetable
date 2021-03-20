package me.ahobson.myandroidtimetable.calendar

class CalendarItem (val startHour: Int,
                    val startMinute: Int,
                    val durationMinutes: Int,
                    val courseTitle: String,
                    val rawRoom: String,
                    val classType: ClassType) {
    override fun toString() = "$courseTitle at $startHour"

    val room = rawRoom
}
