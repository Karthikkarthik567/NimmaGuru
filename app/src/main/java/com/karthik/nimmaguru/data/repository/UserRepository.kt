package com.karthik.nimmaguru.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.karthik.nimmaguru.data.model.User
import com.karthik.nimmaguru.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    // --- Standard Operations ---

    suspend fun saveUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.userId).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(userId: String): Result<User?> {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            Result.success(snapshot.toObject(User::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeUser(userId: String): Flow<User?> {
        return usersCollection.document(userId)
            .snapshots()
            .map { it.toObject(User::class.java) }
    }

    // --- 100x Search & Discovery Features ---

    /**
     * SEARCH GURUS BY VILLAGE
     * Success Criterion: "Students find a mentor in their own village/street."
     * Logic: Filters the global user list for only those with the GURU role in a specific village.
     */
    fun getGurusByVillage(village: String): Flow<List<User>> {
        return usersCollection
            .whereEqualTo("role", UserRole.GURU.value)
            .whereEqualTo("village", village)
            .orderBy("name", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(User::class.java) }
    }

    /**
     * GLOBAL SEARCH FOR STUDENTS
     * Allows students to search all Gurus if their specific village doesn't have one yet.
     */
    fun searchGurus(query: String): Flow<List<User>> {
        return usersCollection
            .whereEqualTo("role", UserRole.GURU.value)
            .orderBy("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(User::class.java) }
    }

    /**
     * ELDERLY-FRIENDLY INITIALS
     * A helper to ensure we always have a profile image placeholder.
     */
    suspend fun updateProfileImage(userId: String, url: String): Result<Unit> {
        return try {
            usersCollection.document(userId).update("profileImageUrl", url).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}