package me.ahobson.myandroidtimetable

import android.R
import android.R.color
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import me.ahobson.myandroidtimetable.calendar.CalendarClickListener
import me.ahobson.myandroidtimetable.calendar.CalendarDay
import me.ahobson.myandroidtimetable.calendar.CalendarItem
import java.util.*
import kotlin.collections.ArrayList


@SuppressLint("ClickableViewAccessibility")
class CalendarDayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var _calendarDay: CalendarDay? = null
    private val calendarItemList: MutableList<CalendarItem> = ArrayList<CalendarItem>()
    private val clashIndexes: MutableMap<CalendarItem, Int> = HashMap<CalendarItem, Int>()
    private val clashCount: MutableMap<CalendarItem, Int> = HashMap<CalendarItem, Int>()
    private val hitBoxes: MutableMap<CalendarItem, RectF> = HashMap<CalendarItem, RectF>()

    init {
        this.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                for (calEvent in hitBoxes.keys) {
                    val hitBox: RectF = hitBoxes.getOrElse(calEvent, {RectF()})
                    if (hitBox.contains(event.x, event.y)) {
                        if (context is CalendarClickListener) {
                            context.clickedCalendarItem(calEvent, event.rawX, event.rawY)
                        }
                    }
                }
            }
            true
        }
    }

    var calendarDay: CalendarDay?
        get() = _calendarDay
        set(value) {
            _calendarDay = value
            computeTimetableOrder()
            updateHitBoxes()
        }

    private var shortSummary: String = ""

    private fun computeTimetableOrder() {
        calendarItemList.clear()
        clashCount.clear()
        clashIndexes.clear()

        if (calendarDay != null) {
            calendarItemList.addAll(_calendarDay!!.classes)
            calendarItemList.sortBy {
                it.startHour * 60 + it.startMinute
            }
        }

        val temporaryList: List<CalendarItem> = calendarItemList.sortedBy {
            it.startHour * 60 + it.startMinute + it.durationMinutes
        }

        var i = 0
        while (i < temporaryList.size) {
            var sameStartTime: Boolean = (i < (calendarItemList.size - 1) &&
                    calendarItemList[i].startHour == calendarItemList[i+1].startHour &&
                    calendarItemList[i].startMinute == calendarItemList[i+1].startMinute)
                    || (i > 0 && calendarItemList[i].startHour == calendarItemList[i-1].startHour &&
                    calendarItemList[i].startMinute == calendarItemList[i-1].startMinute)
            if (temporaryList[i] != calendarItemList[i] || sameStartTime) {
                var clashIndex = 0
                var singleClashList: MutableList<CalendarItem> = ArrayList<CalendarItem>()
                while (i < temporaryList.size && (temporaryList[i] != calendarItemList[i] || sameStartTime)) {
                    clashIndexes[calendarItemList[i]] = clashIndex
                    singleClashList.add(calendarItemList[i])
                    clashIndex++
                    i++
                    sameStartTime = (i < (calendarItemList.size - 1) &&
                    calendarItemList[i].startHour == calendarItemList[i+1].startHour &&
                            calendarItemList[i].startMinute == calendarItemList[i+1].startMinute)
                    || (i < temporaryList.size && i > 0 && calendarItemList[i].startHour == calendarItemList[i-1].startHour &&
                            calendarItemList[i].startMinute == calendarItemList[i-1].startMinute)
                }
                for (clash in singleClashList) {
                    clashCount[clash] = clashIndex
                }
            }
            i++
        }
    }

    private fun updateHitBoxes() {
        val contentWidth = width - paddingLeft - paddingRight

        for (event in calendarItemList) {
            getHitBox(event, contentWidth)
        }
    }

    private fun getHitBox(event: CalendarItem, contentWidth: Int): RectF {
        val top = (event.startHour * 120 + 2 * event.startMinute).toFloat()
        val clashIndex = clashIndexes.getOrElse(event, {0})
        val clashTotal = clashCount.getOrElse(event, {1})
        val objectWidth = contentWidth.toFloat() / clashTotal
        val objectLeftOffset = (contentWidth.toFloat() / clashTotal) * clashIndex

        val hitBox = RectF(objectLeftOffset, top, objectLeftOffset + objectWidth, top + event.durationMinutes * 2)
        hitBoxes[event] = hitBox
        return hitBox
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        for (i in 0..23) {
            val y = i * 120 + 0.5
            canvas.drawLine(0f, y.toFloat(), contentWidth.toFloat(), y.toFloat(), calendarGridPaint)
        }
        canvas.drawLine(0.5.toFloat(), 0.toFloat(), 0.5.toFloat(), contentHeight.toFloat(), calendarGridPaint)

        for (event in calendarItemList) {
            val top = (event.startHour * 120 + 2 * event.startMinute).toFloat()
            val clashIndex = clashIndexes.getOrElse(event, {0})
            val clashTotal = clashCount.getOrElse(event, {1})
            val objectWidth = contentWidth.toFloat() / clashTotal
            val objectLeftOffset = (contentWidth.toFloat() / clashTotal) * clashIndex

            val rect = getHitBox(event, contentWidth)
            drawEvent(canvas, rect, event)
        }
    }

    private fun drawEvent(canvas: Canvas, rect: RectF, calendarItem: CalendarItem) {
        canvas.save();
        canvas.clipRect(rect)
        canvas.drawRoundRect(rect, 6.toFloat(), 6.toFloat(), calendarEventPaint)
        val fontHeight = calendarTextPaint.textSize
        canvas.drawText(calendarItem.courseTitle, rect.left, rect.top + fontHeight, calendarTextPaint)
        canvas.drawText(calendarItem.rawRoom, rect.left, rect.top + (fontHeight * 2), calendarTextPaint)
        canvas.drawText(calendarItem.classType.toString().toLowerCase().capitalize(), rect.left, rect.top + (fontHeight * 3), calendarTextPaint)
        canvas.restore();
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, 24 * 120)
    }

    private val calendarGridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = context!!.getColor(me.ahobson.myandroidtimetable.R.color.gray)
    }

    private val calendarEventPaint = Paint(0).apply {
        style = Paint.Style.FILL
        color = context!!.getColor(me.ahobson.myandroidtimetable.R.color.amber_700)
    }

    private val calendarTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context!!.getColor(R.color.white)
        textSize = 30f
    }

}