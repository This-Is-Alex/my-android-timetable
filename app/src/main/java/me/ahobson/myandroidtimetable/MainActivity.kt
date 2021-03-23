package me.ahobson.myandroidtimetable

import android.animation.Animator
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import me.ahobson.myandroidtimetable.calendar.CalendarClickListener
import me.ahobson.myandroidtimetable.calendar.CalendarItem
import me.ahobson.myandroidtimetable.fragments.ClassSummaryFragment
import me.ahobson.myandroidtimetable.fragments.SettingsFragment


class MainActivity : AppCompatActivity(), CalendarClickListener, AlexsExitListener {
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

    private fun showCalendarItem(calItem: CalendarItem, animationX: Float, animationY: Float) {
        val bundle: Bundle = Bundle()
        bundle.putSerializable("item", calItem)
        bundle.putInt("posX", animationX.toInt())
        bundle.putInt("posY", animationY.toInt())
        var fragment =
            ClassSummaryFragment()
        fragment.arguments = bundle

        supportFragmentManager.commit {
            add(R.id.fragment_container, fragment, "classSummary")
            addToBackStack("classSummary")
        }
    }

    override fun clickedCalendarItem(item: CalendarItem, rawX: Float, rawY: Float) {
        showCalendarItem(item, rawX, rawY)
    }

    override fun exitedClassFragment(fragment: ClassSummaryFragment) {
        supportFragmentManager.popBackStack()
    }
}