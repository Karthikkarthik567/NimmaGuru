package com.karthik.nimmaguru.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karthik.nimmaguru.data.model.Guru
import com.karthik.nimmaguru.data.repository.UserRepository
import com.karthik.nimmaguru.data.repository.GuruRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SearchUiState {
    object Loading : SearchUiState
    object Empty : SearchUiState
    data class Success(val gurus: List<Guru>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val guruRepo: GuruRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSkill = MutableStateFlow<String?>(null)
    val selectedSkill: StateFlow<String?> = _selectedSkill.asStateFlow()

    // 100x Feature: Real-time village context
    private val userVillage = MutableStateFlow<String?>(null)

    /**
     * 100x Engine: Combines real-time Firestore stream with UI filters.
     * Includes 'debounce' to make searching feel smooth on village networks.
     */
    @OptIn(FlowPreview::class)
    val uiState: StateFlow<SearchUiState> = combine(
        guruRepo.getGurusFlow(),
        _searchQuery.debounce(300), // Wait for user to stop typing
        _selectedSkill,
        userVillage
    ) { gurus, query, skill, village ->

        val filtered = gurus.filter { guru ->
            val matchesQuery = query.isEmpty() ||
                    guru.name.contains(query, ignoreCase = true) ||
                    guru.village.contains(query, ignoreCase = true)

            val matchesSkill = skill == null || guru.skills.contains(skill)

            matchesQuery && matchesSkill
        }.sortedByDescending { guru ->
            // Smart Ranking: Local Gurus + Verified Gurus first
            var priority = 0
            if (guru.village.equals(village, ignoreCase = true)) priority += 10
            if (guru.isVerified) priority += 5
            priority
        }

        when {
            filtered.isEmpty() && query.isNotEmpty() -> SearchUiState.Empty
            filtered.isEmpty() && !_isLoading.value -> SearchUiState.Empty
            else -> SearchUiState.Success(filtered)
        }
    }.catch { e ->
        _error.value = e.message
        emit(SearchUiState.Error(e.message ?: "Unknown Error"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState.Loading
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun toggleSkillFilter(skill: String) {
        _selectedSkill.value = if (_selectedSkill.value == skill) null else skill
    }

    /**
     * Sets the context for "Local Discovery"
     */
    fun setUserVillage(village: String) {
        userVillage.value = village
    }
}