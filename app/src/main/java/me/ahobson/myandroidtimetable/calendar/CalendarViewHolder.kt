package me.ahobson.myandroidtimetable.calendar

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.ahobson.myandroidtimetable.R
import java.util.*

class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dayTitle: TextView = itemView.findViewById(R.id.this_day_title)

    fun bind(month: Int, day: Int, dayOfWeek: Int, calendarDay: CalendarDay) {

        dayTitle.text = formatDateString(month, day, dayOfWeek)

        //if it's today
        if (position + 1 == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
            dayTitle.setTypeface(null, Typeface.BOLD)
        } else {
            //reset it because RecyclerView do be like that
            dayTitle.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun formatDateString(month: Int, day: Int, dayOfWeek: Int): String {
        val dayString = if (day < 10) "0$day" else day.toString()
        val monthString = if (month < 10) "0$month" else month.toString()

        val dayOfWeekString = when (dayOfWeek) {
            1 -> "Sun"
            2 -> "Mon"
            3 -> "Tue"
            4 -> "Wed"
            5 -> "Thu"
            6 -> "Fri"
            7 -> "Sat"
            else -> "Error"
        }

        return "$dayString/$monthString\n$dayOfWeekString"
    }
}