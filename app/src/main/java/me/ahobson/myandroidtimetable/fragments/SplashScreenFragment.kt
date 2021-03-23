package me.ahobson.myandroidtimetable.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import me.ahobson.myandroidtimetable.R

class SplashScreenFragment : Fragment {

    var message: String
        set(value: String) {
            view?.findViewById<TextView>(R.id.splashStatus)?.text = value
            field = value
        }

    constructor(message: String): super() {
        this.message = message
    }

    constructor(): super() {
        this.message = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        view.findViewById<TextView>(R.id.splashStatus).text = message
        return view
    }
}