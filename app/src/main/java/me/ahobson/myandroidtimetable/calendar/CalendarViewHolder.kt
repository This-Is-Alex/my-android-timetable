package me.ahobson.myandroidtimetable.calendar

import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.ahobson.myandroidtimetable.CalendarDayView
import me.ahobson.myandroidtimetable.R
import java.util.*

class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val dayTitle: TextView = itemView.findViewById(R.id.this_day_title)
    private val calendarDayWidget: CalendarDayView = itemView.findViewById(R.id.calendar_day_view)

    fun bind(dayOfYear: Int, calendarDay: CalendarDay) {

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        dayTitle.text = formatDateString(month+1, day, dayOfWeek)

        //if it's today
        if (dayOfYear == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
            dayTitle.setTypeface(null, Typeface.BOLD)
            dayTitle.setBackgroundColor(itemView.resources.getColor(R.color.amber_700, null))
        } else {
            //reset it because RecyclerView do be like that
            dayTitle.setTypeface(null, Typeface.NORMAL)
            dayTitle.setBackgroundColor(itemView.resources.getColor(R.color.red_900, null))
        }

        calendarDayWidget.calendarDay = calendarDay
    }

    private fun formatDateString(month: Int, day: Int, dayOfWeek: Int): String {
        val dayString = if (day < 10) "0$day" else day.toString()
        val monthString = if (month < 10) "0$month" else month.toString()

        val dayOfWeekResourceId = when (dayOfWeek) {
            1 -> R.string.sunday_short
            2 -> R.string.monday_short
            3 -> R.string.tuesday_short
            4 -> R.string.wednesday_short
            5 -> R.string.thursday_short
            6 -> R.string.friday_short
            7 -> R.string.saturday_short
            else -> R.string.wip
        }

        val dayOfWeekString = itemView.context.getString(dayOfWeekResourceId)

        return "$dayString/$monthString\n$dayOfWeekString"
    }
}