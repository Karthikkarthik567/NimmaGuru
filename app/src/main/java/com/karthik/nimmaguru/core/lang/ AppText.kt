package com.karthik.nimmaguru.core.lang

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/**
 * 100x Localization Engine
 * Centralized strings for English and Kannada.
 */
object AppText {

    private val isKn get() = LanguageManager.isKannada.value

    // --- General Labels ---
    val appName get() = if (isKn) "ನಿಮ್ಮಗುರು" else "NimmaGuru"
    val findGuru get() = if (isKn) "ಗುರುಗಳನ್ನು ಹುಡುಕಿ" else "Find a Guru"
    val home get() = if (isKn) "ಮುಖಪುಟ" else "Home"

    // --- Session Labels ---
    val sessions get() = if (isKn) "ತರಗತಿಗಳು" else "Sessions"
    val createSession get() = if (isKn) "ಹೊಸ ತರಗತಿ ರಚಿಸಿ" else "Create Session"
    val joinSession get() = if (isKn) "ಸೇರ್ಪಡೆಯಾಗಿ" else "Join Class"
    val seatsLeft get() = if (isKn) "ಸೀಟುಗಳು ಬಾಕಿ ಇವೆ" else "seats left"

    // --- Community Labels ---
    val wallOfFame get() = if (isKn) "ಕೃತಜ್ಞತೆ ಗೋಡೆ" else "Wall of Fame"
    val appreciation get() = if (isKn) "ಮೆಚ್ಚುಗೆ" else "Appreciation"

    // --- Profile & Settings ---
    val profile get() = if (isKn) "ನನ್ನ ಮಾಹಿತಿ" else "My Profile"
    val logout get() = if (isKn) "ಲಾಗ್ ಔಟ್" else "Logout"
    val saveChanges get() = if (isKn) "ಉಳಿಸಿ" else "Save Changes"

    // --- Search Context ---
    val searchPlaceholder get() = if (isKn) "ವಿಷಯ ಅಥವಾ ಊರಿನ ಹೆಸರನ್ನು ಹುಡುಕಿ..." else "Search by skill or village..."
}