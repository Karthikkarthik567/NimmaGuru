package com.karthik.nimmaguru.data.model

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

/**
 * Nimma-Guru User Model
 * Optimized for local village discovery and mentorship workflows.
 *
 * NOTE: 'createdAt' remains a Long to match existing Firestore data
 * seen in Screenshot 2026-05-09 155544.png.
 */
@IgnoreExtraProperties
@Parcelize
data class User(
    @get:PropertyName("user_id") @set:PropertyName("user_id")
    var userId: String = "",

    val name: String = "",

    @get:PropertyName("role") @set:PropertyName("role")
    var roleString: String = UserRole.STUDENT.value,

    val village: String = "",
    val bio: String = "",

    @get:PropertyName("phone_number") @set:PropertyName("phone_number")
    var phoneNumber: String = "",

    @get:PropertyName("profile_image_url") @set:PropertyName("profile_image_url")
    var profileImageUrl: String = "",

    // Validated against Firestore screenshot: type is java.lang.Long
    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Long = System.currentTimeMillis()
) : Parcelable {

    // --- Business Logic & UI Helpers ---

    val role: UserRole get() = UserRole.fromString(roleString)
    val isGuru: Boolean get() = role == UserRole.GURU
    val isStudent: Boolean get() = role == UserRole.STUDENT

    /**
     * Ensures mandatory fields are present for rural discovery.
     */
    val isProfileComplete: Boolean
        get() = name.isNotBlank() && village.isNotBlank() && phoneNumber.isNotBlank()

    /**
     * Returns a localized display name.
     * Supports Kannada for rural accessibility.
     */
    fun getDisplayName(isKannada: Boolean = false): String {
        return when {
            name.isNotBlank() -> name
            isKannada -> "ಹೆಸರಿಲ್ಲದ ಬಳಕೆದಾರ"
            else -> "Anonymous User"
        }
    }

    /**
     * Generates initials for placeholder avatars (e.g., "Karthik N" -> "KN").
     */
    val nameInitials: String
        get() = name.trim().split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
}