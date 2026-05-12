package com.karthik.nimmaguru.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.karthik.nimmaguru.data.model.Session
import com.karthik.nimmaguru.data.model.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()
    private val sessionsCollection = db.collection("sessions")

    /**
     * 100x VILLAGE CALENDAR
     * Satisfies: "View upcoming local sessions at the Samudaya Bhavana."
     * Limits the feed to the user's specific village to reduce data usage.
     */
    fun getVillageSessionsFlow(village: String): Flow<List<Session>> {
        val today = Date()
        return sessionsCollection
            .whereEqualTo("location", village) // Or a specific community center
            .whereGreaterThanOrEqualTo("dateTime", today)
            .orderBy("dateTime", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(Session::class.java)?.copy(sessionId = it.id) }
            }
    }

    /**
     * 100x ATOMIC JOIN
     * Logic: Safely increments the student count ONLY if seats are still available.
     * This prevents over-booking at the Samudaya Bhavana.
     */
    suspend fun joinSession(sessionId: String, studentId: String): Result<Unit> {
        return try {
            val sessionRef = sessionsCollection.document(sessionId)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(sessionRef)
                val currentCount = snapshot.getLong("currentStudentsCount") ?: 0
                val maxCount = snapshot.getLong("maxStudents") ?: 20

                if (currentCount < maxCount) {
                    transaction.update(sessionRef, "currentStudentsCount", FieldValue.increment(1))
                    // Here we could also add the studentId to a 'bookings' sub-collection
                } else {
                    throw Exception("Session is already full!")
                }
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * LATEST RECENT ACTIVITY
     * For the "Wall of Fame" or Home Screen to show recently completed 'Gyaan-Daan'.
     */
    fun getRecentlyCompletedSessions(): Flow<List<Session>> {
        return sessionsCollection
            .whereEqualTo("status", SessionStatus.COMPLETED.value)
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .limit(10)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Session::class.java) }
    }

    suspend fun saveSession(session: Session): Result<Unit> {
        return try {
            val docRef = if (session.sessionId.isEmpty()) sessionsCollection.document() else sessionsCollection.document(session.sessionId)
            docRef.set(session.copy(sessionId = docRef.id)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUpcomingSessionsFlow(): Flow<List<Session>> {
        val today = Date()
        return sessionsCollection
            .whereGreaterThanOrEqualTo("dateTime", today)
            .orderBy("dateTime", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(Session::class.java)?.copy(sessionId = it.id) }
            }
    }
}