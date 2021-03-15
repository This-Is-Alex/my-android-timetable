package me.ahobson.myandroidtimetable

import android.R.layout
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import me.ahobson.myandroidtimetable.calendar.CalendarDay


class MainActivity : AppCompatActivity() {
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
                changeFragment(1)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun changeFragment(id: Int) {
        var fragment: Fragment = MainFragment()
        var name: String = "main";
        if (id == 1) {
            fragment = SettingsFragment()
            name = "settings"
        } else if (id == 2) {

        }
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            setCustomAnimations(
                R.anim.frag_change,
                R.anim.frag_change,
                R.anim.frag_change,
                R.anim.frag_change
            )
            addToBackStack(name)
        }
    }
}