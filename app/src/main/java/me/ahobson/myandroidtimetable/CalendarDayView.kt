package me.ahobson.myandroidtimetable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import me.ahobson.myandroidtimetable.calendar.CalendarDay
import me.ahobson.myandroidtimetable.calendar.CalendarItem


class CalendarDayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var _calendarDay: CalendarDay? = null

    var calendarDay: CalendarDay?
        get() = _calendarDay
        set(value) {
            _calendarDay = value

        }

    private var shortSummary: String = ""

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        for (i in 0..23) {
            val y = i * 120 + 0.5
            canvas.drawLine(0f, y.toFloat(), contentWidth.toFloat(), y.toFloat(), calendarGridPaint)
        }
        canvas.drawLine(0.5.toFloat(), 0.toFloat(), 0.5.toFloat(), contentHeight.toFloat(), calendarGridPaint)

        if (_calendarDay != null && _calendarDay?.classes != null) {
            for (event in _calendarDay!!.classes!!) {
                val top = (event.startHour * 120 + 2 * event.startMinute).toFloat()
                val rect = RectF(0.toFloat(), top, contentWidth.toFloat(), top + event.durationMinutes * 2)
                drawEvent(canvas, rect, event)
                //TODO clashes
            }
        }
    }

    private fun drawEvent(canvas: Canvas, rect: RectF, calendarItem: CalendarItem) {
        canvas.save();
        canvas.clipRect(rect)
        canvas.drawRoundRect(rect, 6.toFloat(), 6.toFloat(), calendarEventPaint)
        canvas.drawText(calendarItem.courseTitle, rect.left, rect.top + calendarTextPaint.textSize, calendarTextPaint)
        canvas.drawText(calendarItem.rawRoom, rect.left, rect.top + (calendarTextPaint.textSize * 2), calendarTextPaint)
        canvas.restore();
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, 24 * 120)
    }

    private val calendarGridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = context!!.getColor(R.color.gray)
    }

    private val calendarEventPaint = Paint(0).apply {
        style = Paint.Style.FILL
        color = context!!.getColor(R.color.amber_700)
    }

    private val calendarTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context!!.getColor(R.color.white)
        textSize = 30f
    }

}