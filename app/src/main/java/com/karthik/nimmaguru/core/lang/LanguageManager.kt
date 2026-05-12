package com.karthik.nimmaguru.core.lang

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

/**
 * 100x Global Language Manager
 * Handles real-time switching and remembers user preference.
 */
object LanguageManager {

    // Internal state - default to false (English)
    private val _isKannada = mutableStateOf(false)

    // Public read-only state for UI observation
    val isKannada: State<Boolean> = _isKannada

    /**
     * Toggles the language and should save to SharedPreferences in a real app.
     */
    fun toggleLanguage() {
        _isKannada.value = !_isKannada.value
        // 100x: Logic to save this to local storage goes here
        // saveToPrefs(_isKannada.value)
    }

    /**
     * Call this during App Startup (Splash Screen) to load saved preference.
     */
    fun loadLanguagePreference(savedAsKannada: Boolean) {
        _isKannada.value = savedAsKannada
    }
}