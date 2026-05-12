package com.karthik.nimmaguru.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.karthik.nimmaguru.data.model.Appreciation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppreciationRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()
    private val appreciationsCollection = db.collection("appreciations")
    private val gurusCollection = db.collection("gurus")

    /**
     * 100x ADD APPRECIATION (Atomic Transaction)
     * Logic: Adds the note AND increments the Guru's reputation score simultaneously.
     */
    suspend fun addAppreciation(appreciation: Appreciation): Result<Unit> {
        return try {
            val appreciationDoc = appreciationsCollection.document()
            val guruDoc = gurusCollection.document(appreciation.toGuruId)

            val finalAppreciation = appreciation.copy(id = appreciationDoc.id)

            db.runTransaction { transaction ->
                // 1. Write the Appreciation
                transaction.set(appreciationDoc, finalAppreciation)

                // 2. Update Guru's Impact (Incrementing total students taught/ranking)
                // We use increment(1) to avoid reading the document first (saves cost)
                transaction.update(guruDoc, "totalStudentsTaught", com.google.firebase.firestore.FieldValue.increment(1))
                transaction.update(guruDoc, "rating", com.google.firebase.firestore.FieldValue.increment(0.1)) // Small boost for every thank you

                null // Transaction requires a return value
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 100x OPTIMIZED FEED (Village Scope)
     * Usually, students care more about Gurus in their village.
     * I added a Village filter to make the "Wall of Fame" more relevant.
     */
    fun getVillageAppreciationsFlow(village: String): Flow<List<Appreciation>> {
        return appreciationsCollection
            .whereEqualTo("isHidden", false)
            // Note: This requires a Firestore Index (village_1_timestamp_-1)
            .whereEqualTo("village", village)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(30)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(Appreciation::class.java) }
            }
    }

    /**
     * GLOBAL WALL OF FAME
     */
    fun getGlobalAppreciationsFlow(): Flow<List<Appreciation>> {
        return appreciationsCollection
            .whereEqualTo("isHidden", false)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(Appreciation::class.java) }
            }
    }

    suspend fun getAppreciationsForGuru(guruId: String): List<Appreciation> {
        return try {
            appreciationsCollection
                .whereEqualTo("toGuruId", guruId)
                .whereEqualTo("isHidden", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Appreciation::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}