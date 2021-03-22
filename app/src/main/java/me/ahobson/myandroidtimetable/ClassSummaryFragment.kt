package me.ahobson.myandroidtimetable

import android.R.layout
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class ClassSummaryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val calitem = requireArguments().getSerializable("item")
        return inflater.inflate(R.layout.fragment_class_summary, container, false)
    }
}