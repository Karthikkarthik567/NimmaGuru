package com.karthik.nimmaguru.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Nimma-Guru Session Model
 * Optimized for village-level scheduling at Samudaya Bhavana.
 * Updated to use Firebase Timestamp to match Firestore data types.
 */
@IgnoreExtraProperties
@Parcelize
data class Session(
    @get:PropertyName("session_id") @set:PropertyName("session_id")
    var sessionId: String = "",

    @get:PropertyName("guru_id") @set:PropertyName("guru_id")
    var guruId: String = "",

    @get:PropertyName("guru_name") @set:PropertyName("guru_name")
    var guruName: String = "",

    val title: String = "",
    val description: String = "",
    val location: String = "Samudaya Bhavana",

    // 🔥 Matches the Timestamp type seen in Firestore console
    @get:PropertyName("date_time") @set:PropertyName("date_time")
    var dateTime: Timestamp = Timestamp.now(),

    @get:PropertyName("duration_minutes") @set:PropertyName("duration_minutes")
    var durationMinutes: Int = 60,

    @get:PropertyName("max_students") @set:PropertyName("max_students")
    var maxStudents: Int = 20,

    @get:PropertyName("current_students_count") @set:PropertyName("current_students_count")
    var currentStudentsCount: Int = 0,

    @get:PropertyName("status") @set:PropertyName("status")
    var statusString: String = SessionStatus.UPCOMING.value,

    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Timestamp = Timestamp.now()
) : Parcelable {

    // Derived Properties
    val status: SessionStatus get() = SessionStatus.fromString(statusString)
    val isFull: Boolean get() = currentStudentsCount >= maxStudents
    val seatsLeft: Int get() = (maxStudents - currentStudentsCount).coerceAtLeast(0)

    /**
     * Converts the Firebase Timestamp to a localized String.
     * Supports Kannada for rural accessibility.
     */
    fun getFormattedDate(isKannada: Boolean = false): String {
        val date = dateTime.toDate() // Convert Timestamp to Date for formatting
        val locale = if (isKannada) Locale("kn", "IN") else Locale.getDefault()
        val pattern = if (isKannada) "MMM dd | HH:mm" else "MMM dd | hh:mm a"
        val sdf = SimpleDateFormat(pattern, locale)
        return sdf.format(date)
    }

    /**
     * Automated Live Detection:
     * Calculates if the session is currently happening based on the start time and duration.
     */
    val isCurrentlyLive: Boolean get() {
        if (status != SessionStatus.UPCOMING && status != SessionStatus.ONGOING) return false
        val startTime = dateTime.toDate().time
        val endTime = startTime + (durationMinutes * 60 * 1000)
        val now = System.currentTimeMillis()
        return now in startTime..endTime
    }

    val canJoin: Boolean get() = status == SessionStatus.UPCOMING && !isFull && !isCurrentlyLive
}