package me.ahobson.myandroidtimetable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.ahobson.myandroidtimetable.calendar.CalendarDay
import me.ahobson.myandroidtimetable.calendar.CalendarItem
import me.ahobson.myandroidtimetable.calendar.CalendarViewHolder
import java.util.*

class CalendarAdapter(private val calendarDays: Array<CalendarDay>)
    : RecyclerView.Adapter<CalendarViewHolder>() {

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private val blankDay = CalendarDay(2000, 1, 1, mutableListOf<CalendarItem>())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.calendar_day_holder, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, position + 1)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        var calendarDay: CalendarDay = blankDay
        for (calDay: CalendarDay in calendarDays) {
            if (calDay.happensOnDay(currentYear, month, day)) {
                calendarDay = calDay
                break
            }
        }

        holder.bind(position + 1, calendarDay)
    }

    override fun getItemCount() = 365
}
