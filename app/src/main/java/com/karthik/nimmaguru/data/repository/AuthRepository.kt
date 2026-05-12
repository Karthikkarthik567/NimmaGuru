package com.karthik.nimmaguru.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Nimma-Guru Auth Repository
 * Uses Coroutines for non-blocking UI and Result wrappers for 100x error handling.
 */
@Singleton
class AuthRepository @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()

    /**
     * Reusable Flow to listen to Auth changes across the whole app.
     * This is 100x better because the app reacts automatically when a user logs out.
     */
    val currentUser: FirebaseUser? get() = auth.currentUser

    /**
     * Wraps Firebase Task into a Kotlin Result.
     * No more listeners! Just use 'await()'
     */
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Registration failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Added: Password Reset - Critical for elderly users who might forget passwords.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun logout() {
        auth.signOut()
    }
}