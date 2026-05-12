package com.karthik.nimmaguru.core.ai

import com.karthik.nimmaguru.data.model.Guru

/**
 * 100x Intelligent Matchmaking
 * Uses a multi-factor weighted algorithm to surface the most impactful mentors.
 */
object AIRecommender {

    // Scoring Weights (Fine-tuned for Rural Mentorship)
    private const val WEIGHT_EXACT_SKILL = 15
    private const val WEIGHT_VILLAGE_PROXIMITY = 10
    private const val WEIGHT_VERIFIED_STATUS = 8
    private const val WEIGHT_HIGH_BIO_QUALITY = 3
    private const val WEIGHT_SESSION_EXPERIENCE = 2 // Boost per 5 sessions

    fun rankGurus(
        targetSkills: List<String>,
        userVillage: String,
        gurus: List<Guru>
    ): List<Guru> {
        return gurus.sortedByDescending { guru ->
            var finalScore = 0

            // 1. Skill Relevance (The 'Gyaan' Factor)
            // We reward mentors who have exactly what the student seeks.
            guru.skills.forEach { skill ->
                if (targetSkills.any { it.equals(skill, ignoreCase = true) }) {
                    finalScore += WEIGHT_EXACT_SKILL
                }
            }

            // 2. Local Proximity (The 'Nimma' Factor)
            // In rural tech, proximity reduces the barrier to meet.
            if (guru.village.equals(userVillage, ignoreCase = true)) {
                finalScore += WEIGHT_VILLAGE_PROXIMITY
            }

            // 3. Trust & Verification
            // Verified Gurus have gone through community vetting.
            if (guru.isVerified) {
                finalScore += WEIGHT_VERIFIED_STATUS
            }

            // 4. Content Quality
            // High-quality bios indicate a mentor's commitment to explaining their value.
            if (guru.bio.length > 50) {
                finalScore += WEIGHT_HIGH_BIO_QUALITY
            }

            // 5. Impact History
            // Reward Gurus who are active in the community.
            val experienceBonus = (guru.sessionsCompleted / 5) * WEIGHT_SESSION_EXPERIENCE
            finalScore += experienceBonus

            finalScore
        }
    }
}