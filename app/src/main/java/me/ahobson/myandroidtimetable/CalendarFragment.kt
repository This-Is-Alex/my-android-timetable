package me.ahobson.myandroidtimetable

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.ahobson.myandroidtimetable.calendar.CalendarDay
import me.ahobson.myandroidtimetable.calendar.CalendarItem
import me.ahobson.myandroidtimetable.calendar.ClassType
import java.util.*


class CalendarFragment : Fragment() {

    override fun onStart() {
        super.onStart()

        val testCalendar: Array<CalendarDay> = arrayOf(
            CalendarDay(2021, 3, 19, arrayOf(
                CalendarItem(10, 30, 30, "SENG440", "Engineering Core E8", ClassType.LECTURE)
            ))
        )
        val calAdapter = CalendarAdapter(testCalendar)
        val recyclerView: RecyclerView? = activity?.findViewById(R.id.calendar_recycler_view)

        //adapted from https://stackoverflow.com/questions/51201482/android-percent-screen-width-in-recyclerview-item/51224889
        val layoutManager = object : LinearLayoutManager(context) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                lp.width = width / 7
                return true
            }
        }

        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = calAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        val timeOfDayLayout: LinearLayout? = view.findViewById(R.id.calendar_time_of_day)

        //dummy padding
        val paddingStart = createLabel()
        paddingStart.height = 60
        timeOfDayLayout?.addView(paddingStart)

        for (i in 1..23) {
            val timeOfDayLabel = createLabel()
            timeOfDayLabel.text = formatHour(i)
            timeOfDayLayout?.addView(timeOfDayLabel)
        }

        //dummy padding
        val paddingEnd = createLabel()
        paddingEnd.height = 60
        timeOfDayLayout?.addView(paddingEnd)

        return view
    }

    private fun createLabel(): TextView {
        val timeOfDayLabel = TextView(context)
        timeOfDayLabel.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        timeOfDayLabel.gravity = Gravity.CENTER
        timeOfDayLabel.height = 120
        timeOfDayLabel.setTextColor(resources.getColor(R.color.white, null))
        timeOfDayLabel.setBackgroundColor(resources.getColor(R.color.red_900, null))
        return timeOfDayLabel
    }

    private fun formatHour(hour: Int): String {
        //TODO check if display 24-hour times
        return if (hour < 10) "0$hour:00" else "$hour:00"
    }
}