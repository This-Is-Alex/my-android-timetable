package me.ahobson.myandroidtimetable

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import me.ahobson.myandroidtimetable.calendar.CalendarClickListener
import me.ahobson.myandroidtimetable.calendar.CalendarDay
import me.ahobson.myandroidtimetable.calendar.CalendarItem
import me.ahobson.myandroidtimetable.fragments.*
import me.ahobson.myandroidtimetable.io.CalendarDownloader
import java.io.Serializable


class MainActivity : AppCompatActivity(), CalendarClickListener, AlexsExitListener, AlexsLoginListener {

    var isSignedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragManager = supportFragmentManager
        if(fragManager.findFragmentByTag("calendar") == null) {
            val preferences = getPreferences(Context.MODE_PRIVATE)
            val url = preferences.getString("url", "error")
            if (url != null && url != "error") {
                loginWithUrl(url)
            } else {
                showLoginPage()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isSignedIn) {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.main_menu, menu)
        }
        return isSignedIn
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                val preferences = getPreferences(Context.MODE_PRIVATE)
                val url = preferences.getString("url", "error")
                if (url != null && url != "error") {
                    loginWithUrl(url)
                }
                true
            }
            R.id.logout -> {
                val editor = getPreferences(Context.MODE_PRIVATE).edit()
                editor.remove("url")
                editor.apply()
                val downloader = CalendarDownloader(this)
                downloader.deleteOfflineCalendar()
                isSignedIn = false
                invalidateOptionsMenu()
                showLoginPage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSplashScreen(message: String) {
        supportActionBar?.hide()
        var fragment = SplashScreenFragment(message)
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
        }
    }

    private fun showLoginPage() {
        supportActionBar?.show()
        var fragment = SignInFragment()
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            setTransition( FragmentTransaction.TRANSIT_FRAGMENT_CLOSE )
        }
    }

    private fun showMainPage(calendar: ArrayList<CalendarDay>) {
        supportActionBar?.show()
        var fragment = MainFragment()

        intent.putExtra("calendar", calendar)

        // don't want to be able to go back to splash screen or login screen
        for (i in 0..supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment, "calendar")
            setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
        }
    }

    private fun showCalendarItem(calItem: CalendarItem, animationX: Float, animationY: Float) {
        val bundle = Bundle()
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
        supportFragmentManager.popBackStackImmediate()
    }

    override fun onBackPressed() {
        val topFragment = supportFragmentManager.fragments.last()
        if (topFragment is ClassSummaryFragment) {
            topFragment.closeFragment()
        } else {
            super.onBackPressed()
        }
    }

    override fun loginWithUrl(url: String) {
        showSplashScreen(getString(R.string.progress_downloading_calendar))

        val downloader = CalendarDownloader(this)
        lifecycleScope.launch {
            var success = false
            if (downloader.download(url)) {
                success = true
            } else if (downloader.offlineCalendarExists()) {
                Toast.makeText(downloader.context, getString(R.string.offline_calendar), Toast.LENGTH_SHORT)
                    .show()
                success = downloader.loadFromFile()
            }
            if (success) {
                invalidateOptionsMenu()
                isSignedIn = true
                val editor = getPreferences(Context.MODE_PRIVATE).edit()
                editor.putString("url", url)
                editor.apply()
                val calendar: ArrayList<CalendarDay> = downloader.getCalendar()
                var classCount = 0
                for (day in calendar) {
                    classCount += day.classes.size
                }
                Toast.makeText(downloader.context, getString(R.string.loaded_calendar).replace("%COUNT%", classCount.toString()), Toast.LENGTH_SHORT)
                    .show()
                showMainPage(calendar)
            } else {
                Toast.makeText(downloader.context, getString(R.string.failed_calendar), Toast.LENGTH_SHORT).show()
                showLoginPage()
            }
        }
    }
}