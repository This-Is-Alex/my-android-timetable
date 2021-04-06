package me.ahobson.myandroidtimetable.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import me.ahobson.myandroidtimetable.R
import me.ahobson.myandroidtimetable.calendar.CalendarClickListener
import me.ahobson.myandroidtimetable.calendar.CalendarDay
import me.ahobson.myandroidtimetable.calendar.CalendarItem
import java.util.*

class TodayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_today, container, false)
        val theActivity: Activity = requireActivity()
        val cardList: LinearLayout = view.findViewById(R.id.today_list)
        cardList.removeAllViews()

        var hasAddedClass = false

        if (theActivity.intent != null && theActivity.intent!!.hasExtra("calendar")) {
            val calendarList = theActivity.intent!!.getSerializableExtra("calendar") as ArrayList<CalendarDay>

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            for (calendarDay in calendarList) {
                if (calendarDay.happensOnDay(year, month, day)) {
                    for (event in calendarDay.classes) {
                        hasAddedClass = true
                        val card = createOneClassCard(event)
                        cardList.addView(card)
                    }
                    break
                }
            }
        }

        if (!hasAddedClass) {
            val imageView = ImageView(context)
            imageView.adjustViewBounds = true
            imageView.setImageResource(R.drawable.no_classes_today)
            cardList.addView(imageView)
        }

        return view
    }

    private fun createOneClassCard(calendarItem: CalendarItem): View {
        val view = layoutInflater.inflate(R.layout.layout_one_class_today, null)

        view.findViewById<TextView>(R.id.one_class_time).text = calendarItem.getFormattedTimeWithoutDuration()
        view.findViewById<TextView>(R.id.one_class_classtitle).text = calendarItem.courseTitle
        view.findViewById<TextView>(R.id.one_class_location).text = calendarItem.room

        var dragged = false
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (!dragged) {
                    val context = context
                    if (context is CalendarClickListener) {
                        context.clickedCalendarItem(calendarItem, view.x + view.width / 2, event.rawY)
                    }
                    view.performClick()
                }
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                dragged = false
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                dragged = true
            }
            true
        }

        return view
    }
}