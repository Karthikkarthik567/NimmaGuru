package com.karthik.nimmaguru.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

/**
 * Nimma-Guru Appreciation Model
 * Captures "Gyaan-Daan" (Knowledge Donation) gratitude.
 * Aligned with Firestore structure in Screenshot 2026-05-09 155621.png.
 */
@IgnoreExtraProperties
@Parcelize
data class Appreciation(
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("fromUserId") @set:PropertyName("fromUserId")
    var fromUserId: String = "",

    @get:PropertyName("fromUserName") @set:PropertyName("fromUserName")
    var fromUserName: String = "",

    @get:PropertyName("fromUserImage") @set:PropertyName("fromUserImage")
    var fromUserImage: String? = null,

    @get:PropertyName("toGuruId") @set:PropertyName("toGuruId")
    var toGuruId: String = "",

    @get:PropertyName("toGuruName") @set:PropertyName("toGuruName")
    var toGuruName: String = "",

    @get:PropertyName("sessionId") @set:PropertyName("sessionId")
    var sessionId: String = "",

    @get:PropertyName("sessionTitle") @set:PropertyName("sessionTitle")
    var sessionTitle: String = "",

    val message: String = "",

    // Matches the Timestamp type in the Firestore console screenshot
    val timestamp: Timestamp = Timestamp.now(),

    @get:PropertyName("isHidden") @set:PropertyName("isHidden")
    var isHidden: Boolean = false,

    @get:PropertyName("isFeatured") @set:PropertyName("isFeatured")
    var isFeatured: Boolean = false,

    @get:PropertyName("highlightColor") @set:PropertyName("highlightColor")
    var highlightColor: String = "#FDFCF8"
) : Parcelable {

    /**
     * UI Helper: Relative time with Kannada support.
     * Essential for the village social feed feel.
     */
    fun getRelativeTime(isKannada: Boolean = false): String {
        val now = Timestamp.now().seconds
        val diff = now - timestamp.seconds

        return when {
            diff < 60 -> if (isKannada) "ಈಗ ತಾನೇ" else "Just now"
            diff < 3600 -> {
                val mins = diff / 60
                if (isKannada) "$mins ನಿಮಿಷಗಳ ಹಿಂದೆ" else "${mins}m ago"
            }
            diff < 86400 -> {
                val hours = diff / 3600
                if (isKannada) "$hours ಗಂಟೆಗಳ ಹಿಂದೆ" else "${hours}h ago"
            }
            else -> {
                val days = diff / 86400
                if (isKannada) "$days ದಿನಗಳ ಹಿಂದೆ" else "${days}d ago"
            }
        }
    }

    /**
     * Guru Ranking Logic: Heartfelt messages provide more impact score.
     */
    val sentimentWeight: Int
        get() = when {
            message.length > 100 -> 3
            message.length > 50 -> 2
            else -> 1
        }

    val isValid: Boolean get() = message.isNotBlank() && message.trim().length >= 5
}