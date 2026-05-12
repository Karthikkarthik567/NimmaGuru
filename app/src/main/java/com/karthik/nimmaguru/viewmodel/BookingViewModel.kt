package com.karthik.nimmaguru.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karthik.nimmaguru.data.model.Session
import com.karthik.nimmaguru.data.repository.AuthRepository
import com.karthik.nimmaguru.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BookingUiState {
    object Idle : BookingUiState()
    object Loading : BookingUiState()
    object Success : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepo: BookingRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _bookingState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingState: StateFlow<BookingUiState> = _bookingState.asStateFlow()

    // 100x Feature: Real-time list of sessions the student has joined
    val myBookedSessions: StateFlow<List<Session>> = authRepo.getCurrentUserId()?.let { userId ->
        bookingRepo.observeStudentSessions(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    } ?: MutableStateFlow(emptyList())

    /**
     * 100x ATOMIC BOOKING
     * Handles the full transaction with immediate UI feedback.
     */
    fun bookSession(session: Session) {
        val studentId = authRepo.getCurrentUserId() ?: run {
            _bookingState.value = BookingUiState.Error("Please log in to join.")
            return
        }

        if (_bookingState.value is BookingUiState.Loading) return

        _bookingState.value = BookingUiState.Loading

        viewModelScope.launch {
            // The repository now handles the 'Already Booked' and 'Capacity' checks
            // inside a single Firestore Transaction for 100x reliability.
            val result = bookingRepo.bookSession(
                sessionId = session.sessionId,
                studentId = studentId,
                guruId = session.guruId
            )

            result.onSuccess {
                _bookingState.value = BookingUiState.Success
            }.onFailure { error ->
                _bookingState.value = BookingUiState.Error(error.message ?: "Failed to join class.")
            }
        }
    }

    fun resetBookingState() {
        _bookingState.value = BookingUiState.Idle
    }
}