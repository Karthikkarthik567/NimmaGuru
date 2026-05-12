package com.karthik.nimmaguru.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karthik.nimmaguru.core.lang.LanguageManager
import com.karthik.nimmaguru.data.model.User
import com.karthik.nimmaguru.data.model.UserRole
import com.karthik.nimmaguru.data.repository.AuthRepository
import com.karthik.nimmaguru.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Nimma-Guru Authentication ViewModel
 * Manages the state and logic for Login, Registration, and Password Resets.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    // Internal mutable state flow for UI updates
    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    // Exposed read-only state for Compose screens
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    /**
     * REGISTRATION LOGIC
     * Executes a two-step process: Creating an account and then saving the profile.
     */
    fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole,
        village: String
    ) {
        // Validation check before starting network calls
        if (name.isBlank() || email.isBlank() || password.length < 6 || village.isBlank()) {
            _authState.value = AuthUiState.Error("Please fill all fields accurately.")
            return
        }

        _authState.value = AuthUiState.Loading

        viewModelScope.launch {
            // STEP 1: Firebase Authentication Registration
            val authResult = authRepo.register(email, password)

            authResult.fold(
                onSuccess = { firebaseUser ->
                    // STEP 2: Firestore Profile Creation
                    val user = User(
                        userId = firebaseUser.uid,
                        name = name,
                        roleString = role.value,
                        village = village,
                        createdAt = System.currentTimeMillis()
                    )

                    val dbResult = userRepo.saveUser(user)

                    if (dbResult.isSuccess) {
                        _authState.value = AuthUiState.Success
                    } else {
                        // Handle DB failure separately for clarity
                        _authState.value = AuthUiState.Error(
                            dbResult.exceptionOrNull()?.message ?: "Profile save failed"
                        )
                    }
                },
                onFailure = { e ->
                    // Handle Authentication failure (e.g., email already in use)
                    _authState.value = AuthUiState.Error(e.message ?: "Authentication failed")
                }
            )
        }
    }

    /**
     * LOGIN LOGIC
     */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthUiState.Error("Email and Password are required.")
            return
        }

        _authState.value = AuthUiState.Loading

        viewModelScope.launch {
            val result = authRepo.login(email, password)
            if (result.isSuccess) {
                _authState.value = AuthUiState.Success
            } else {
                _authState.value = AuthUiState.Error(
                    result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }

    /**
     * PASSWORD RESET LOGIC
     * Multi-language support included for localized feedback messages.
     */
    fun resetPassword(email: String) {
        if (email.isBlank()) {
            val error = if (LanguageManager.isKannada.value) "ಇಮೇಲ್ ಅಗತ್ಯವಿದೆ" else "Email is required"
            _authState.value = AuthUiState.Error(error)
            return
        }

        _authState.value = AuthUiState.Loading

        viewModelScope.launch {
            val result = authRepo.sendPasswordReset(email)
            if (result.isSuccess) {
                val msg = if (LanguageManager.isKannada.value)
                    "ಪಾಸ್‌ವರ್ಡ್ ರಿಸೆಟ್ ಲಿಂಕ್ ಕಳುಹಿಸಲಾಗಿದೆ!"
                else
                    "Password reset email sent!"
                _authState.value = AuthUiState.Error(msg) // Displaying success as an "Error" state is a shortcut to show a toast/dialog
            } else {
                _authState.value = AuthUiState.Error(
                    result.exceptionOrNull()?.message ?: "Reset failed"
                )
            }
        }
    }

    /**
     * Resets the authentication state back to Idle.
     * Crucial to call this after successful navigation to prevent recomposition loops.
     */
    fun resetState() {
        _authState.value = AuthUiState.Idle
    }
}

/**
 * State management representing the current status of the authentication flow.
 */
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}