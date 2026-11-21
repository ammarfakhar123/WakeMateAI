package com.wakemate.ai.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(calendar.time)
    }

    fun formatHourMinute(hour: Int, minute: Int): Pair<String, String> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        val amPmFormat = SimpleDateFormat("a", Locale.getDefault())
        return Pair(timeFormat.format(calendar.time), amPmFormat.format(calendar.time).uppercase())
    }

    fun formatDate(timestampMillis: Long): String {
        val format = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        return format.format(Date(timestampMillis))
    }

    fun formatDateTime(timestampMillis: Long): String {
        val format = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        return format.format(Date(timestampMillis))
    }

    fun formatMessageTime(timestampMillis: Long): String {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(Date(timestampMillis))
    }

    fun getDaysString(daysOfWeek: List<Int>): String {
        if (daysOfWeek.isEmpty()) return "Once"
        if (daysOfWeek.size == 7) return "Every day"
        if (daysOfWeek.size == 5 && !daysOfWeek.contains(1) && !daysOfWeek.contains(7)) return "Weekdays"
        if (daysOfWeek.size == 2 && daysOfWeek.contains(1) && daysOfWeek.contains(7)) return "Weekends"

        val dayNames = mapOf(
            1 to "Sun", 2 to "Mon", 3 to "Tue", 4 to "Wed", 5 to "Thu", 6 to "Fri", 7 to "Sat"
        )
        return daysOfWeek.sorted().mapNotNull { dayNames[it] }.joinToString(", ")
    }

    /**
     * Computes the exact epoch millis for the next alarm trigger.
     */
    fun calculateNextTriggerMillis(hour: Int, minute: Int, daysOfWeek: List<Int>): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (daysOfWeek.isEmpty()) {
            // One-time alarm
            if (target.before(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            return target.timeInMillis
        }

        // Recurring alarm: find the closest scheduled day
        val currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK)
        for (i in 0..7) {
            val checkDay = ((currentDayOfWeek - 1 + i) % 7) + 1
            if (daysOfWeek.contains(checkDay)) {
                val candidate = Calendar.getInstance().apply {
                    timeInMillis = target.timeInMillis
                    add(Calendar.DAY_OF_YEAR, i)
                }
                if (candidate.after(now)) {
                    return candidate.timeInMillis
                }
            }
        }

        // Fallback: next week
        target.add(Calendar.DAY_OF_YEAR, 7)
        return target.timeInMillis
    }

    fun getTimeRemainingString(targetMillis: Long): String {
        val diff = targetMillis - System.currentTimeMillis()
        if (diff <= 0) return "Now"
        val hours = diff / (3600 * 1000)
        val minutes = (diff % (3600 * 1000)) / (60 * 1000)
        return when {
            hours > 0 -> "In ${hours}h ${minutes}m"
            else -> "In ${minutes}m"
        }
    }
}
