package me.ahobson.myandroidtimetable

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.ahobson.myandroidtimetable.calendar.CalendarItem

class OneClassToday : Fragment() {

    var calendarItem: CalendarItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null && savedInstanceState.containsKey("item")) {
            calendarItem = savedInstanceState.getSerializable("item") as CalendarItem
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_one_class_today, container, false)

        if (calendarItem != null) {
            view.findViewById<TextView>(R.id.one_class_time).text = calendarItem!!.getFormattedTime()
        }

        return view
    }
}