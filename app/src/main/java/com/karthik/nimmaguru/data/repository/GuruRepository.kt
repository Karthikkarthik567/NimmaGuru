package com.karthik.nimmaguru.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.karthik.nimmaguru.data.model.Guru
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuruRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()
    private val gurusCollection = db.collection("gurus")

    /**
     * 100x REAL-TIME FEED
     * Uses '.snapshots()' for a cleaner, more modern Flow implementation than callbackFlow.
     */
    fun getGurusFlow(): Flow<List<Guru>> {
        return gurusCollection
            .orderBy("sessionsCompleted", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(Guru::class.java)?.copy(guruId = it.id) }
            }
    }

    suspend fun getGuruById(guruId: String): Result<Guru?> {
        return try {
            val snapshot = gurusCollection.document(guruId).get().await()
            val guru = snapshot.toObject(Guru::class.java)?.copy(guruId = snapshot.id)
            Result.success(guru)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 100x MULTI-FILTER SEARCH
     * Satisfies: "Skill Filter using ChipGroups" & "Village search."
     * Note: Complex queries with orderBy require a Firestore Composite Index.
     */
    suspend fun filterGurus(
        selectedSkills: List<String>,
        village: String? = null
    ): Result<List<Guru>> {
        return try {
            var query: Query = gurusCollection

            // 100x Logic: Handle multiple skills if the student selects multiple chips
            if (selectedSkills.isNotEmpty()) {
                // Firestore supports 'whereIn' for up to 10 items in an array check
                query = query.whereArrayContainsAny("skills", selectedSkills)
            }

            if (!village.isNullOrBlank()) {
                query = query.whereEqualTo("village", village)
            }

            // Always prioritize high-impact Gurus for the Wall of Fame effect
            query = query.orderBy("sessionsCompleted", Query.Direction.DESCENDING)

            val result = query.get().await()
            val list = result.toObjects(Guru::class.java)
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * IMPACT GOAL FEATURE: Get "Top Gurus" for the Home Screen Wall of Fame.
     */
    fun getTopMentors(limit: Long = 5): Flow<List<Guru>> {
        return gurusCollection
            .whereEqualTo("isVerified", true)
            .orderBy("sessionsCompleted", Query.Direction.DESCENDING)
            .limit(limit)
            .snapshots()
            .map { it.toObjects(Guru::class.java) }
    }

    suspend fun saveGuru(guru: Guru): Result<Unit> {
        return try {
            // Using userId as the document ID ensures a 1:1 mapping between User and Guru data
            gurusCollection.document(guru.userId).set(guru).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}