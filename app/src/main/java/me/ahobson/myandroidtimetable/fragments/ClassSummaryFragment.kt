package me.ahobson.myandroidtimetable.fragments

import android.animation.Animator
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import me.ahobson.myandroidtimetable.AlexsExitListener
import me.ahobson.myandroidtimetable.R
import me.ahobson.myandroidtimetable.calendar.CalendarItem
import kotlin.math.hypot


class ClassSummaryFragment : Fragment() {

    private var posX = 0
    private var posY = 0
    private var listener: AlexsExitListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        posX = requireArguments().getInt("posX")
        posY = requireArguments().getInt("posY")

        val rootView: View = inflater.inflate(R.layout.fragment_class_summary, container, false)

        if (requireArguments().containsKey("item") && requireArguments().getSerializable("item") is CalendarItem) {
            val calitem: CalendarItem = requireArguments().getSerializable("item") as CalendarItem

            rootView.findViewById<TextView>(R.id.class_summary_coursetitle).text = calitem.courseTitle
            rootView.findViewById<TextView>(R.id.class_summary_room).text = calitem.room
            rootView.findViewById<TextView>(R.id.class_summary_date).text = calitem.getFormattedTime()
            rootView.findViewById<TextView>(R.id.class_summary_classtype).text = calitem.getLocalisedClassType(requireContext())
        }

        rootView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                v.removeOnLayoutChangeListener(this)

                // get the hypothenuse so the radius is from one corner to the other
                val radius = hypot(right.toDouble(), bottom.toDouble()).toInt()
                val reveal =
                    ViewAnimationUtils.createCircularReveal(v, posX, posY, 0f, radius.toFloat())
                reveal.interpolator = DecelerateInterpolator(2f)
                reveal.duration = 1000
                reveal.start()
            }
        })

        rootView.setOnTouchListener { _, _ ->
            closeFragment()
            rootView.performClick()
            true
        }

        return rootView
    }

    fun closeFragment() {
        val unreveal: Animator =
            this.prepareUnrevealAnimator(posX, posY)

        unreveal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                invokeClosedCallback()
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        unreveal.start()
    }

    private fun invokeClosedCallback() {
        if (listener != null) {
            listener!!.exitedClassFragment(this)
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (activity is AlexsExitListener) {
            listener = activity
        }
    }

    /**
     * Adapted from https://gist.github.com/ferdy182/d9b3525aa65b5b4c468a
     * Get the animator to unreveal the circle
     *
     * @param cx center x of the circle (or where the view was touched)
     * @param cy center y of the circle (or where the view was touched)
     * @return Animator object that will be used for the animation
     */
    private fun prepareUnrevealAnimator(
        cx: Int,
        cy: Int
    ): Animator {
        val radius: Int? = getEnclosingCircleRadius(requireView(), cx, cy)
        val anim = ViewAnimationUtils.createCircularReveal(
            view,
            cx,
            cy,
            radius!!.toFloat(),
            0f
        )
        anim.interpolator = AccelerateInterpolator(2f)
        anim.duration = 350
        return anim
    }

    /**
     * Adapted from https://gist.github.com/ferdy182/d9b3525aa65b5b4c468a
     * To be really accurate we have to start the circle on the furthest corner of the view
     *
     * @param v  the view to unreveal
     * @param cx center x of the circle
     * @param cy center y of the circle
     * @return the maximum radius
     */
    private fun getEnclosingCircleRadius(v: View, cx: Int, cy: Int): Int? {
        val realCenterX = cx + v.left
        val realCenterY = cy + v.top
        val distanceTopLeft = hypot(
            realCenterX - v.left.toDouble(),
            realCenterY - v.top.toDouble()
        ).toInt()
        val distanceTopRight = hypot(
            v.right - realCenterX.toDouble(),
            realCenterY - v.top.toDouble()
        ).toInt()
        val distanceBottomLeft = hypot(
            realCenterX - v.left.toDouble(),
            v.bottom - realCenterY.toDouble()
        ).toInt()
        val distanceBottomRight = hypot(
            v.right - realCenterX.toDouble(),
            v.bottom - realCenterY.toDouble()
        ).toInt()
        val distances = arrayOf(
            distanceTopLeft, distanceTopRight, distanceBottomLeft,
            distanceBottomRight
        )
        return distances.max()
    }
}