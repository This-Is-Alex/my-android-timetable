package me.ahobson.myandroidtimetable.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.ahobson.myandroidtimetable.R
import me.ahobson.myandroidtimetable.animation.startCircularReveal


class ClassSummaryFragment : Fragment() {

    var posX: Int? = null
    var posY: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        posX = requireArguments().getInt("posX")
        posY = requireArguments().getInt("posY")

        val calitem = requireArguments().getSerializable("item")
        //TODO

        return inflater.inflate(R.layout.fragment_class_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (posX != null && posY != null) {
            view.startCircularReveal(posX!!, posY!!)
        }
    }
}