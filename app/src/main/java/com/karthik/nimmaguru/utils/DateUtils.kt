package com.karthik.nimmaguru.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    /**
     * Formats a Firebase Timestamp into a friendly date for the Session Card.
     * Example: "12 May, 10:30 AM" or "ಮೇ 12, 10:30 AM"
     */
    fun formatSessionDate(timestamp: Timestamp?, isKannada: Boolean = false): String {
        if (timestamp == null) return ""
        val date = timestamp.toDate()

        val pattern = if (isKannada) "MMM dd, hh:mm a" else "dd MMM, hh:mm a"
        val locale = if (isKannada) Locale("kn", "IN") else Locale.getDefault()

        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.format(date)
    }

    /**
     * Relative time for the Appreciation Card ("2 hours ago", etc.)
     */
    fun getRelativeTime(timestamp: Timestamp?): String {
        if (timestamp == null) return "Recently"
        val now = System.currentTimeMillis()
        val diff = now - timestamp.toDate().time

        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000}m ago"
            diff < 86400_000 -> "${diff / 3600_000}h ago"
            else -> "${diff / 86400_000}d ago"
        }
    }
}