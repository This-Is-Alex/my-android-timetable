package me.ahobson.myandroidtimetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.ahobson.myandroidtimetable.calendar.CalendarDay
import java.util.*


class CalendarFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        val testCalendar: Array<CalendarDay> = arrayOf(
                CalendarDay(Date(2021, 3, 24), arrayOf()),
                CalendarDay(Date(2021, 3, 23), arrayOf()),
                CalendarDay(Date(2021, 3, 22), arrayOf())
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
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }
}