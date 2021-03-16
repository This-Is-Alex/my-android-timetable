package me.ahobson.myandroidtimetable.calendar

import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.ahobson.myandroidtimetable.R
import java.util.*

class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val dayTitle: TextView = itemView.findViewById(R.id.this_day_title)
    private val calendarDayWidget: LinearLayout = itemView.findViewById(R.id.calendar_day)

    fun bind(month: Int, day: Int, dayOfWeek: Int, calendarDay: CalendarDay) {

        dayTitle.text = formatDateString(month, day, dayOfWeek)

        //if it's today
        if (position + 1 == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
            dayTitle.setTypeface(null, Typeface.BOLD)
        } else {
            //reset it because RecyclerView do be like that
            dayTitle.setTypeface(null, Typeface.NORMAL)
        }

        calendarDayWidget.removeAllViews()
        for (i in 0..23) {
            val timeOfDayLabel = createGridLabel()
            timeOfDayLabel.text = ""

            calendarDayWidget.addView(timeOfDayLabel)
        }
    }

    private fun createGridLabel(): TextView {
        val timeOfDayLabel = TextView(itemView.context)
        timeOfDayLabel.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        timeOfDayLabel.gravity = Gravity.RIGHT.or(Gravity.TOP)
        timeOfDayLabel.height = 100
        timeOfDayLabel.setTextColor(itemView.resources.getColor(R.color.white, null))
        timeOfDayLabel.setBackgroundResource(R.drawable.medium_border_bottom_right)
        return timeOfDayLabel
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