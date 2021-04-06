package me.ahobson.myandroidtimetable.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.ahobson.myandroidtimetable.CalendarAdapter
import me.ahobson.myandroidtimetable.R
import me.ahobson.myandroidtimetable.calendar.CalendarDay
import java.util.*

class CalendarFragment : Fragment() {

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

        var calendarList = ArrayList<CalendarDay>()
        val theActivity: Activity = requireActivity()
        if (theActivity.intent != null && theActivity.intent!!.hasExtra("calendar")) {
            calendarList = theActivity.intent!!.getSerializableExtra("calendar") as ArrayList<CalendarDay>
            Log.d("CalendarFragment", "Loaded fragment with ${calendarList.size} days")
            for (day in calendarList) {
                Log.d("CalendarFragment", "${day.getDateString()}: ${day.classes.size} events")
                for (event in day.classes) {
                    Log.d("CalendarFragment", "${event.courseTitle} at ${event.startHour}:${event.startMinute} for ${event.durationMinutes}mins in ${event.room}")
                }
            }
        }
        val calAdapter =
                CalendarAdapter(calendarList.toTypedArray())

        val recyclerView: RecyclerView = view.findViewById(R.id.calendar_recycler_view)

        //adapted from https://stackoverflow.com/questions/51201482/android-percent-screen-width-in-recyclerview-item/51224889
        val layoutManager = object : LinearLayoutManager(context) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                lp.width = width / 7
                return true
            }
        }

        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = calAdapter

        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_WEEK)
        recyclerView.scrollToPosition(dayOfYear)

        return view
    }

    private fun createLabel(): TextView {
        val timeOfDayLabel = TextView(context)
        timeOfDayLabel.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        timeOfDayLabel.gravity = Gravity.CENTER
        timeOfDayLabel.height = 120
        timeOfDayLabel.setTextColor(resources.getColor(R.color.white, null))
        timeOfDayLabel.setBackgroundColor(resources.getColor(R.color.red_900, null))
        return timeOfDayLabel
    }

    private fun formatHour(hour: Int): String {
        return if (hour < 10) "0$hour:00" else "$hour:00"
    }
}