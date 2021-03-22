package me.ahobson.myandroidtimetable

import android.R.layout
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import me.ahobson.myandroidtimetable.calendar.CalendarClickListener
import me.ahobson.myandroidtimetable.calendar.CalendarDay
import me.ahobson.myandroidtimetable.calendar.CalendarItem


class MainActivity : AppCompatActivity(), CalendarClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                showSettings()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSettings() {
        var fragment = SettingsFragment()
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
            addToBackStack("settings")
        }
    }

    fun showCalendarItem(calItem: CalendarItem, animationX: Float, animationY: Float) {
        val bundle: Bundle = Bundle()
        bundle.putSerializable("item", calItem)
        var fragment = ClassSummaryFragment()
        fragment.arguments = bundle
        supportFragmentManager.commit {
            //setCustomAnimations(R.anim.frag_change, R.anim.frag_change)
            replace(R.id.fragment_container, fragment, "classSummary")
            addToBackStack("classSummary")
        }
    }

    override fun clickedCalendarItem(item: CalendarItem, rawX: Float, rawY: Float) {
        showCalendarItem(item, rawX, rawY)
    }
}