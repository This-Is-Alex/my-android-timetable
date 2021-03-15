package me.ahobson.myandroidtimetable.calendar

class CalendarItem (private val date: String,
                    private val startHour: Int,
                    private val startMinute: Int,
                    private val durationMinutes: Int,
                    private val courseTitle: String,
                    private val rawRoom: String,
                    private val classType: ClassType) {
    override fun toString() = "$courseTitle on $date"
}
