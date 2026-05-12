package com.karthik.nimmaguru.utils

import com.karthik.nimmaguru.data.model.User
import com.karthik.nimmaguru.data.model.UserRole

/**
 * 100x Recommendation Engine
 * Scores Gurus to ensure the most relevant community members rise to the top.
 */
object GuruScoring {

    // Scoring Constants for easy fine-tuning
    private const val WEIGHT_VILLAGE_MATCH = 100
    private const val WEIGHT_SKILL_MATCH = 50
    private const val WEIGHT_VERIFIED = 30
    private const val WEIGHT_SESSION_COMPLETED = 5 // Per session

    /**
     * Calculates a ranking score for a Guru.
     */
    fun scoreGuru(
        user: User,
        studentVillage: String,
        targetSkill: String? = null
    ): Int {
        var totalScore = 0

        // 1. Proximity Match (The Core of Nimma-Guru)
        if (user.village.equals(studentVillage, ignoreCase = true)) {
            totalScore += WEIGHT_VILLAGE_MATCH
        }

        // 2. Skill Logic (Fallback to Bio-matching since User isn't the Guru model)
        // If targetSkill is in the bio, they are relevant.
        targetSkill?.let { skill ->
            if (user.bio.contains(skill, ignoreCase = true)) {
                totalScore += WEIGHT_SKILL_MATCH
            }
        }

        // 3. Trust & Impact Factor
        // If this User object has these fields (or via linked Guru profile)
        if (user.role == UserRole.GURU) {
            // Note: In a production app, you'd join this with Guru-specific data
            // but for now, we can check a 'isVerified' flag if added to User.
        }

        return totalScore
    }

    /**
     * Helper to sort a list of Users based on the score
     */
    fun rankGurus(
        gurus: List<User>,
        studentVillage: String,
        targetSkill: String? = null
    ): List<User> {
        return gurus.sortedByDescending { scoreGuru(it, studentVillage, targetSkill) }
    }
}