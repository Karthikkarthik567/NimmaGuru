package com.karthik.nimmaguru.data.model

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

/**
 * Nimma-Guru Mentor Model
 * Optimized for discovery, reputation, and rural scheduling.
 */
@IgnoreExtraProperties
@Parcelize
data class Guru(
    val guruId: String = "",
    val userId: String = "",
    val name: String = "",
    val village: String = "",
    val bio: String = "",
    val skills: List<String> = emptyList(),
    val availableDays: List<String> = emptyList(),

    // Reputation & Impact for "Wall of Fame"
    val rating: Double = 5.0,
    val sessionsCompleted: Int = 0,
    val totalStudentsTaught: Int = 0,
    val isVerified: Boolean = false,

    // Structured Availability to help elderly Gurus avoid double-booking
    val availability: List<AvailabilitySlot> = emptyList(),

    val profileImageUrl: String? = null
) : Parcelable {

    // 100x Helper: Prevents empty UI states
    val hasSkills: Boolean get() = skills.isNotEmpty()

    /**
     * Formats skills for the "GuruCard.kt".
     * Logic: Shows first two skills and a count for the rest.
     */
    fun getSkillsPreview(isKannada: Boolean = false): String {
        if (skills.isEmpty()) return if (isKannada) "ಸಾಮಾನ್ಯ ಮಾರ್ಗದರ್ಶಕರು" else "General Mentor"

        val preview = skills.take(2).joinToString(", ")
        val remaining = skills.size - 2

        return if (remaining > 0) {
            val moreText = if (isKannada) " +$remaining ಹೆಚ್ಚಿನವು" else " +$remaining more"
            "$preview$moreText"
        } else {
            preview
        }
    }

    /**
     * Determines the ranking for the "Wall of Fame".
     * Higher weight given to sessions completed and verified status.
     */
    val impactScore: Int
        get() = (sessionsCompleted * 10) + (totalStudentsTaught * 2) + (if (isVerified) 50 else 0)
}

@Parcelize
data class AvailabilitySlot(
    val day: String = "",       // e.g., "Saturday"
    val period: String = "",    // e.g., "Morning"
    val timeRange: String = ""  // e.g., "10:00 AM - 12:00 PM"
) : Parcelable