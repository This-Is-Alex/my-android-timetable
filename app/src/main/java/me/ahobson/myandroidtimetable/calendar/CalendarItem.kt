package me.ahobson.myandroidtimetable.calendar

import android.content.Context
import me.ahobson.myandroidtimetable.R
import java.io.Serializable

class CalendarItem (val startHour: Int,
                    val startMinute: Int,
                    val durationMinutes: Int,
                    val courseTitle: String,
                    val rawRoom: String,
                    val classType: ClassType): Serializable {
    override fun toString() = "$courseTitle at $startHour"

    fun getFormattedTime(): String {
        val endHour = startHour + ((startMinute + durationMinutes) / 60)
        val endMinute = (startMinute + durationMinutes) % 60
        return "${numTo2DigitString(startHour)}:${numTo2DigitString(startMinute)} - " +
                "${numTo2DigitString(endHour)}:${numTo2DigitString(endMinute)} " +
                "($durationMinutes mins)"
    }

    fun getFormattedTimeWithoutDuration(): String {
        val endHour = startHour + ((startMinute + durationMinutes) / 60)
        val endMinute = (startMinute + durationMinutes) % 60
        return "${numTo2DigitString(startHour)}:${numTo2DigitString(startMinute)} - " +
                "${numTo2DigitString(endHour)}:${numTo2DigitString(endMinute)}"
    }

    fun getLocalisedClassType(context: Context): String {
        val resourceId = when(classType) {
            ClassType.LECTURE -> R.string.classtype_lecture
            ClassType.LAB -> R.string.classtype_lab
            ClassType.TUTORIAL -> R.string.classtype_tutorial
            ClassType.TEST -> R.string.classtype_test
            ClassType.WORKSHOP -> R.string.classtype_workshop
            else -> R.string.classtype_other
        }
        return context.getString(resourceId)
    }

    private fun numTo2DigitString(num: Int): String {
        return if (num < 10) {
            "0$num"
        } else {
            "$num"
        }
    }

    val room = rawRoom
}
