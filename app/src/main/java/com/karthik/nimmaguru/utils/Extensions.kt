package com.karthik.nimmaguru.utils

import androidx.compose.ui.graphics.Color

/**
 * Extension to convert Hex Strings to Compose Color safely.
 */
fun String.toComposeColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        Color.Gray // Fallback
    }
}

/**
 * Capitalizes every word (useful for Names and Village titles).
 */
fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }