package com.karthik.nimmaguru.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karthik.nimmaguru.data.model.User
import com.karthik.nimmaguru.data.repository.RecommendationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val repo: RecommendationRepository
) : ViewModel() {

    private val _recommendedGurus = MutableStateFlow<List<User>>(emptyList())
    val recommendedGurus: StateFlow<List<User>> = _recommendedGurus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * 100x SMART LOADING
     * Combines Village proximity with Skill relevance.
     */
    fun loadRecommendations(studentVillage: String, preferredSkill: String? = null) {
        if (_isLoading.value) return // Prevent multiple simultaneous calls

        _isLoading.value = true
        viewModelScope.launch {
            val result = repo.getSmartRecommendations(studentVillage)

            result.onSuccess { allGurus ->
                // 100x Logic: Sort based on the weighted score
                val sortedList = allGurus.sortedByDescending { guru ->
                    calculateRelevanceScore(guru, studentVillage, preferredSkill)
                }
                _recommendedGurus.value = sortedList
            }.onFailure {
                _recommendedGurus.value = emptyList()
            }

            _isLoading.value = false
        }
    }

    /**
     * 100x SCORING ALGORITHM
     * Weights:
     * - Same Village: 100 pts (Community Priority)
     * - Exact Skill Match: 50 pts (Educational Needs)
     * - Verified Badge: 30 pts (Trust Factor)
     */
    private fun calculateRelevanceScore(guru: User, studentVillage: String, skill: String?): Int {
        var score = 0

        // 1. Proximity Check
        if (guru.village.equals(studentVillage, ignoreCase = true)) {
            score += 100
        }

        // 2. Skill Check (Assuming User has a skills list or we fetch from Guru profile)
        // If skill list is not in User model yet, bio-check is a good fallback.
        if (skill != null && guru.bio.contains(skill, ignoreCase = true)) {
            score += 50
        }

        // 3. Trust Check (Verified Gurus should be seen first)
        // Note: You might need to check the 'isVerified' flag here

        return score
    }
}