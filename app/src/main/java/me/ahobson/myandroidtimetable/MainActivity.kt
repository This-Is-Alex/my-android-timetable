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
import me.ahobson.myandroidtimetable.calendar.CalendarItem
import me.ahobson.myandroidtimetable.fragments.*
import me.ahobson.myandroidtimetable.io.CalendarDownloader


class MainActivity : AppCompatActivity(), CalendarClickListener, AlexsExitListener, AlexsLoginListener {

    var isSignedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = getPreferences(Context.MODE_PRIVATE)
        if (preferences.contains("url")) {
            showSplashScreen(getString(R.string.progress_downloading_calendar))
        } else {
            showLoginPage()
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
            R.id.settings -> {
                showSettings()
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

    private fun showSettings() {
        supportActionBar?.show()
        var fragment = SettingsFragment()
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
            addToBackStack("settings")
        }
    }

    private fun showLoginPage() {
        supportActionBar?.show()
        var fragment = SignInFragment()
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
        }
    }

    private fun showMainPage() {
        supportActionBar?.show()
        var fragment = MainFragment()

        // don't want to be able to go back to splash screen or login screen
        for (i in 0..supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
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
        supportActionBar?.hide()
        showCalendarItem(item, rawX, rawY)
    }

    override fun exitedClassFragment(fragment: ClassSummaryFragment) {
        supportFragmentManager.popBackStackImmediate()
        supportActionBar?.show()
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
            if (downloader.download(url)) {
                Toast.makeText(downloader.context, "Loaded "+downloader.getCalendar().size+" items", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(downloader.context, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}