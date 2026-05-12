package com.karthik.nimmaguru.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.karthik.nimmaguru.data.model.Session
import com.karthik.nimmaguru.data.model.SessionStatus
import com.karthik.nimmaguru.data.repository.AuthRepository
import com.karthik.nimmaguru.data.repository.SessionRepository
import com.karthik.nimmaguru.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepo: SessionRepository,
    private val bookingRepo: BookingRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    // Filter state for the calendar
    private val _selectedVillage = MutableStateFlow<String?>(null)

    /**
     * 100x REACTIVE CALENDAR
     * Combines the village filter with the Firestore stream.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val sessions: StateFlow<List<Session>> = _selectedVillage
        .flatMapLatest { village ->
            if (village == null) sessionRepo.getUpcomingSessionsFlow()
            else sessionRepo.getVillageSessionsFlow(village)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<SessionActionState>(SessionActionState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _createState = MutableStateFlow<CreateSessionUiState>(CreateSessionUiState.Idle)
    val createState = _createState.asStateFlow()

    /**
     * 100x ATOMIC BOOKING
     * Logic: Safety first. Prevents double-booking and checks capacity.
     */
    fun joinSession(session: Session) {
        val studentId = authRepo.getCurrentUserId() ?: return

        _uiState.value = SessionActionState.Loading

        viewModelScope.launch {
            // First check if already booked to save data/battery
            val alreadyBooked = bookingRepo.isAlreadyBooked(session.sessionId, studentId)
            if (alreadyBooked) {
                _uiState.value = SessionActionState.Error("You are already registered for this class!")
                return@launch
            }

            val result = bookingRepo.bookSession(
                sessionId = session.sessionId,
                studentId = studentId,
                guruId = session.guruId
            )

            result.onSuccess {
                _uiState.value = SessionActionState.Success("Successfully joined ${session.title}!")
            }.onFailure { e ->
                _uiState.value = SessionActionState.Error(e.message ?: "Failed to join.")
            }
        }
    }

    fun createSession(title: String, description: String, location: String, maxStudents: Int, dateTimeMillis: Long) {
        val guruId = authRepo.getCurrentUserId() ?: return

        _createState.value = CreateSessionUiState.Loading

        viewModelScope.launch {
            val session = Session(
                title = title,
                description = description,
                location = location,
                maxStudents = maxStudents,
                dateTime = Timestamp(Date(dateTimeMillis)),
                guruId = guruId,
                guruName = authRepo.currentUser?.displayName ?: "Guru",
                statusString = SessionStatus.UPCOMING.value
            )

            val result = sessionRepo.saveSession(session)
            result.onSuccess {
                _createState.value = CreateSessionUiState.Success
            }.onFailure { e ->
                _createState.value = CreateSessionUiState.Error(e.message ?: "Failed to publish")
            }
        }
    }

    fun filterByVillage(village: String?) {
        _selectedVillage.value = village
    }

    fun resetState() { _uiState.value = SessionActionState.Idle }

    fun resetCreateState() { _createState.value = CreateSessionUiState.Idle }
}

sealed class SessionActionState {
    object Idle : SessionActionState()
    object Loading : SessionActionState()
    data class Success(val message: String) : SessionActionState()
    data class Error(val message: String) : SessionActionState()
}

sealed class CreateSessionUiState {
    object Idle : CreateSessionUiState()
    object Loading : CreateSessionUiState()
    object Success : CreateSessionUiState()
    data class Error(val message: String) : CreateSessionUiState()
}