package com.karthik.nimmaguru.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.karthik.nimmaguru.data.model.Appreciation
import com.karthik.nimmaguru.data.repository.AppreciationRepository
import com.karthik.nimmaguru.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Nimma-Guru Appreciation ViewModel
 * Manages the "Wall of Fame" with real-time updates and gratitude submission.
 */
@HiltViewModel
class AppreciationViewModel @Inject constructor(
    private val appreciationRepo: AppreciationRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    // 100x Pattern: Real-time list of gratitudes
    private val _allAppreciations = MutableStateFlow<List<Appreciation>>(emptyList())
    val allAppreciations: StateFlow<List<Appreciation>> = _allAppreciations.asStateFlow()

    // UI State for sending a new note
    private val _sendState = MutableStateFlow<SendUiState>(SendUiState.Idle)
    val sendState: StateFlow<SendUiState> = _sendState.asStateFlow()

    init {
        observeWallOfFame()
    }

    /**
     * 100x Feature: Automatic Sync
     * Listens to Firestore changes. No "Refresh" button needed.
     */
    private fun observeWallOfFame() {
        viewModelScope.launch {
            appreciationRepo.getGlobalAppreciationsFlow().collect { list ->
                _allAppreciations.value = list
            }
        }
    }

    /**
     * SEND APPRECIATION
     * Automatically handles User ID and validation.
     */
    fun sendAppreciation(
        toGuruId: String,
        guruName: String,
        message: String,
        studentName: String // Passed from the UserProfile state
    ) {
        val fromUserId = authRepo.getCurrentUserId() ?: return

        if (message.length < 5) {
            _sendState.value = SendUiState.Error("Please write a meaningful note (min 5 chars).")
            return
        }

        _sendState.value = SendUiState.Loading

        viewModelScope.launch {
            val appreciation = Appreciation(
                fromUserId = fromUserId,
                fromUserName = studentName,
                toGuruId = toGuruId,
                toGuruName = guruName,
                sessionTitle = "General Session", // Defaulting for now
                message = message,
                timestamp = Timestamp.now()
            )

            val result = appreciationRepo.addAppreciation(appreciation)

            if (result.isSuccess) {
                _sendState.value = SendUiState.Success
            } else {
                _sendState.value = SendUiState.Error(
                    result.exceptionOrNull()?.message ?: "Could not send note."
                )
            }
        }
    }

    fun resetSendState() {
        _sendState.value = SendUiState.Idle
    }
}

/**
 * State for the "Write Note" UI
 */
sealed class SendUiState {
    object Idle : SendUiState()
    object Loading : SendUiState()
    object Success : SendUiState()
    data class Error(val message: String) : SendUiState()
}