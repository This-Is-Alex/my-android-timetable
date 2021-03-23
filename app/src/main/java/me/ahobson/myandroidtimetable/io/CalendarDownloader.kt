package me.ahobson.myandroidtimetable.io

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.ahobson.myandroidtimetable.calendar.CalendarDay
import me.ahobson.myandroidtimetable.calendar.CalendarItem
import me.ahobson.myandroidtimetable.calendar.ClassType
import java.io.*
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.collections.ArrayList

class CalendarDownloader(val context: Context) {

    private val internalCalendar: MutableList<CalendarDay> = ArrayList<CalendarDay>()
    private val dateParser = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH)

    suspend fun download(url: String): Boolean {
        var result = false
        withContext(Dispatchers.IO) {
            result = downloadCalendar(url)
        }
        return result
    }

    private fun downloadCalendar(url: String): Boolean {
        val connection = URL(url).openConnection() as HttpsURLConnection
        try {
            val calendarContents = connection.inputStream.readBytes()

            Log.println(Log.INFO, "Downloader", "Downloaded file")

            internalCalendar.clear()
            try {
                parseCalendarFile(calendarContents)
            } catch (exception: Exception) {
                Log.println(Log.INFO, "Downloader", "Parsed with exception: "+Log.getStackTraceString(exception))
                return false
            }

            Log.println(Log.INFO, "Downloader", "Parsed calendar")

            return if (internalCalendar.size > 0) {
                context.openFileOutput("calendar.ics", Context.MODE_PRIVATE).use {
                    it.write(calendarContents)
                }
                true
            } else {
                false
            }
        } finally {
            connection.disconnect()
        }
        return false
    }

    fun getCalendar(): List<CalendarDay> {
        return internalCalendar.toList()
    }

    fun loadFromFile() {
        val calendarContents = context.openFileInput("calendar.ics").readBytes()

        internalCalendar.clear()
        parseCalendarFile(calendarContents)
    }

    private fun parseCalendarFile(contents: ByteArray) {
        val reader = BufferedReader(InputStreamReader(ByteArrayInputStream(contents)))
        val lines = reader.readLines()

        var i = 0
        var eventStart = 0
        while (i < lines.size) {
            val line = lines[i]
            if (line == "BEGIN:VEVENT") {
                eventStart = i + 1
            } else if (line == "END:VEVENT") {
                parseCalendarEntry(lines.subList(eventStart, i))
            }
            i++
        }
    }

    private fun parseCalendarEntry(lines: List<String>) {
        var startDate: Date? = null
        var endDate: Date? = null
        var courseTitle: String = ""
        var classType: ClassType? = null
        var location: String = ""

        for (line in lines) {
            val keyValPair = line.split(":")
            if (keyValPair.size >= 2) {
                val key = keyValPair[0]
                val value = line.substring(line.indexOf(":") + 1)
                when (key) {
                    "DTSTART;TZID=Pacific/Auckland" -> {
                        startDate = dateParser.parse(value)
                    }
                    "DTEND;TZID=Pacific/Auckland" -> {
                        endDate = dateParser.parse(value)
                    }
                    "LOCATION" -> {
                        location = value
                    }
                    "DESCRIPTION" -> {
                        val descriptionComponents = value.split("\\,")
                        courseTitle = descriptionComponents[0].substring(
                            0,
                            descriptionComponents[0].indexOf("-")
                        )
                        classType = when (descriptionComponents[1].subSequence(1, 4)) {
                            "Com" -> ClassType.LAB
                            "Lab" -> ClassType.LAB
                            "Lec" -> ClassType.LECTURE
                            "Tes" -> ClassType.TEST
                            "Tut" -> ClassType.TUTORIAL
                            "Wor" -> ClassType.WORKSHOP
                            else -> ClassType.OTHER
                        }
                    }
                }
            }
        }

        if (startDate != null && endDate != null && courseTitle.isNotEmpty() && classType != null && location.isNotEmpty()) {
            val calendarItem = createCalendarItem(startDate, endDate, courseTitle, classType, location)
            if (calendarItem != null) {
                saveCalendarEntry(calendarItem, startDate)
            }
        }
    }

    private fun createCalendarItem(startDate: Date, endDate: Date, courseTitle: String,
                                   classType: ClassType, location: String): CalendarItem? {
        val durationMillis = endDate.time - startDate.time
        var duration = (durationMillis / 1000 / 60).toInt()

        val calendar = Calendar.getInstance()
        calendar.time = startDate
        val startHour = calendar.get(Calendar.HOUR_OF_DAY)
        val startMinute = calendar.get(Calendar.MINUTE)

        var calendarItem: CalendarItem? = null
        try {
            val room = parseRoom(startDate, location)
            calendarItem = CalendarItem(startHour, startMinute, duration, courseTitle, room, classType)
        } catch (exception: ArrayIndexOutOfBoundsException) {
            Log.println(Log.ERROR, "CalendarParser", "ArrayIndexOutOfBoundsException parsing room $location")
        }
        return calendarItem
    }

    private fun parseRoom(startDate: Date, rawRoom: String): String {
        val candidateRooms = rawRoom.split(")\\,")
        for (candidateRoom in candidateRooms) {
            val datesIndex = candidateRoom.indexOf("(")
            if (datesIndex == -1) {
                return candidateRoom
            } else {
                val dateRanges = candidateRoom.substring(datesIndex + 1)
                if (withinRange(startDate, dateRanges)) {
                    return candidateRoom
                }
            }
        }
        return "Unknown"
    }

    private fun withinRange(date: Date, dateRangeString: String): Boolean {
        val candidateDates = dateRangeString.split("\\,")
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        for (candidateDate in candidateDates) {
            if (candidateDate.contains("-")) {
                val dateRangeString = candidateDate.split("-")
                val startDateRange = parseOneLocationDate(dateRangeString[0])
                val endDateRange = parseOneLocationDate(dateRangeString[1])
                if (startDateRange.get(Calendar.DAY_OF_YEAR) <= dayOfYear && endDateRange.get(Calendar.DAY_OF_YEAR) >= dayOfYear) {
                    return true
                }
            } else {
                val day = parseOneLocationDate(candidateDate)
                if (day.get(Calendar.DAY_OF_YEAR) == dayOfYear) {
                    return true
                }
            }
        }
        return false
    }

    private fun parseOneLocationDate(locationDate: String): Calendar {
        val components: List<String> = locationDate.split("/")

        var monthString = if (components[1].endsWith(")")) {
            components[1].substring(0, components[1].length - 1)
        } else {
            components[1]
        }

        val day = Integer.parseInt(components[0].trim())
        val month = Integer.parseInt(monthString.trim())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        //assumes the calendar is for this year so don't worry about leap years
        return calendar
    }

    private fun saveCalendarEntry(entry: CalendarItem, startDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        for (calDay in internalCalendar) {
            if (calDay.happensOnDay(year, month, day)) {
                calDay.classes.add(entry)
                return
            }
        }
        val calDay = CalendarDay(year, month, day, ArrayList<CalendarItem>())
        calDay.classes.add(entry)
        internalCalendar.add(calDay)
    }
}