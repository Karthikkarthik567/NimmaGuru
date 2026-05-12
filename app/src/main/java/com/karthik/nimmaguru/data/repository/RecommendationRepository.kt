package com.karthik.nimmaguru.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.karthik.nimmaguru.data.model.User
import com.karthik.nimmaguru.data.model.UserRole
import com.karthik.nimmaguru.data.model.Guru
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val gurusCollection = db.collection("gurus")

    /**
     * 100x SMART RECOMMENDATIONS
     * Hybrid Logic: Local Community + High Impact Mentors.
     */
    suspend fun getSmartRecommendations(studentVillage: String): Result<List<User>> {
        return try {
            // 1. Prioritize Local Gurus (Village match)
            val localGurus = usersCollection
                .whereEqualTo("role", UserRole.GURU.value)
                .whereEqualTo("village", studentVillage)
                .limit(10)
                .get()
                .await()
                .toObjects(User::class.java)

            // 2. Fetch "Global Stars" (High sessionsCompleted) from the 'gurus' collection
            // We use the 'gurus' collection because 'users' doesn't have impact stats.
            val topGurusDocs = gurusCollection
                .orderBy("sessionsCompleted", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()

            val topGuruUserIds = topGurusDocs.documents.mapNotNull { it.getString("userId") }

            // 3. Fetch the actual User profiles for those Global Stars
            val globalGurus = if (topGuruUserIds.isNotEmpty()) {
                usersCollection
                    .whereIn("userId", topGuruUserIds)
                    .get()
                    .await()
                    .toObjects(User::class.java)
            } else emptyList()

            // 4. Combine: Local Gurus first, then fill with Global Stars
            val combinedList = (localGurus + globalGurus).distinctBy { it.userId }

            Result.success(combinedList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 100x ALPHA-DIRECTORY
     * Fetch all Gurus for the full search list.
     */
    suspend fun getAllGurus(): Result<List<User>> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("role", UserRole.GURU.value)
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()

            Result.success(snapshot.toObjects(User::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}