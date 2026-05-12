package com.karthik.nimmaguru.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karthik.nimmaguru.data.model.Guru
import com.karthik.nimmaguru.data.repository.AuthRepository
import com.karthik.nimmaguru.data.repository.GuruRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class GuruUiState {
    object Idle : GuruUiState()
    object Loading : GuruUiState()
    object Saving : GuruUiState()
    data class Success(val guru: Guru) : GuruUiState()
    data class Error(val message: String) : GuruUiState()
}

@HiltViewModel
class GuruViewModel @Inject constructor(
    private val guruRepo: GuruRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GuruUiState>(GuruUiState.Idle)
    val uiState: StateFlow<GuruUiState> = _uiState.asStateFlow()

    private val _currentGuru = MutableStateFlow<Guru?>(null)
    val currentGuru: StateFlow<Guru?> = _currentGuru.asStateFlow()

    init {
        loadGuruProfile()
    }

    /**
     * 100x LOAD LOGIC
     * If the profile doesn't exist, we provide a default empty Guru object
     * instead of throwing an error, so the user can start filling it in.
     */
    private fun loadGuruProfile() {
        val userId = authRepo.getCurrentUserId() ?: return
        _uiState.value = GuruUiState.Loading

        viewModelScope.launch {
            val result = guruRepo.getGuruById(userId)

            result.onSuccess { guru ->
                if (guru != null) {
                    _currentGuru.value = guru
                    _uiState.value = GuruUiState.Success(guru)
                } else {
                    // New user: No error, just an empty state to fill
                    _uiState.value = GuruUiState.Idle
                }
            }.onFailure { e ->
                _uiState.value = GuruUiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun fetchGuruProfile(guruId: String) {
        _uiState.value = GuruUiState.Loading
        viewModelScope.launch {
            val result = guruRepo.getGuruById(guruId)
            result.onSuccess { guru ->
                if (guru != null) {
                    _uiState.value = GuruUiState.Success(guru)
                } else {
                    _uiState.value = GuruUiState.Error("Guru not found")
                }
            }.onFailure { e ->
                _uiState.value = GuruUiState.Error(e.message ?: "Error loading guru")
            }
        }
    }

    /**
     * 100x SAVE WITH COMPLETION CHECK
     */
    fun saveGuru(
        name: String, // Added name since it's required for discovery
        bio: String,
        skills: List<String>,
        availableDays: List<String>
    ) {
        val userId = authRepo.getCurrentUserId() ?: return

        if (bio.length < 20) {
            _uiState.value = GuruUiState.Error("Please write a bit more about your expertise (min 20 chars)")
            return
        }

        _uiState.value = GuruUiState.Saving

        viewModelScope.launch {
            val updatedGuru = (_currentGuru.value ?: Guru(userId = userId)).copy(
                name = name,
                bio = bio,
                skills = skills,
                availableDays = availableDays,
                // Profile is considered "complete" if it has skills and bio
                isVerified = _currentGuru.value?.isVerified ?: false
            )

            val result = guruRepo.saveGuru(updatedGuru)

            result.onSuccess {
                _currentGuru.value = updatedGuru
                _uiState.value = GuruUiState.Success(updatedGuru)
            }.onFailure { e ->
                _uiState.value = GuruUiState.Error(e.message ?: "Failed to save profile")
            }
        }
    }

    fun resetState() { _uiState.value = GuruUiState.Idle }
}