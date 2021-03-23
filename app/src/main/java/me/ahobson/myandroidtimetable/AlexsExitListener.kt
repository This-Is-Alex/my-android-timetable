package me.ahobson.myandroidtimetable

import androidx.fragment.app.Fragment
import me.ahobson.myandroidtimetable.fragments.ClassSummaryFragment

interface AlexsExitListener {
    fun exitedClassFragment(fragment: ClassSummaryFragment)
}