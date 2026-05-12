package com.karthik.nimmaguru.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.karthik.nimmaguru.data.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()
    private val bookingsCollection = db.collection("bookings")
    private val sessionsCollection = db.collection("sessions")

    /**
     * 100x ATOMIC BOOKING
     * Prevents over-booking and ensures data integrity.
     */
    suspend fun bookSession(
        sessionId: String,
        studentId: String,
        guruId: String
    ): Result<Unit> {
        return try {
            db.runTransaction { transaction ->
                val sessionRef = sessionsCollection.document(sessionId)
                val sessionSnapshot = transaction.get(sessionRef)

                val max = sessionSnapshot.getLong("maxStudents") ?: 0
                val current = sessionSnapshot.getLong("currentStudentsCount") ?: 0

                if (current >= max) {
                    throw Exception("Seat limit reached!")
                }

                // Update count and create booking record in one atomic step
                transaction.update(sessionRef, "currentStudentsCount", FieldValue.increment(1))

                val bookingRef = bookingsCollection.document("${studentId}_${sessionId}")
                val bookingData = mapOf(
                    "bookingId" to bookingRef.id,
                    "sessionId" to sessionId,
                    "studentId" to studentId,
                    "guruId" to guruId,
                    "status" to "booked",
                    "createdAt" to FieldValue.serverTimestamp()
                )
                transaction.set(bookingRef, bookingData)
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 100x OPTIMIZED FETCH: "MY CLASSES"
     * Instead of just IDs, we can use 'whereIn' to fetch full Session objects.
     * This makes the UI populate instantly with class details.
     */
    fun observeStudentSessions(studentId: String): Flow<List<Session>> {
        return bookingsCollection
            .whereEqualTo("studentId", studentId)
            .snapshots()
            .map { bookingSnapshot ->
                val sessionIds = bookingSnapshot.documents.mapNotNull { it.getString("sessionId") }

                if (sessionIds.isEmpty()) {
                    emptyList()
                } else {
                    // Fetch the full details for all these sessions in one batch
                    // Note: Firestore 'whereIn' limit is 30 IDs per query
                    sessionsCollection
                        .whereIn("sessionId", sessionIds.take(30))
                        .get()
                        .await()
                        .toObjects(Session::class.java)
                }
            }
    }

    suspend fun cancelBooking(sessionId: String, studentId: String): Result<Unit> {
        return try {
            db.runTransaction { transaction ->
                val sessionRef = sessionsCollection.document(sessionId)
                val bookingRef = bookingsCollection.document("${studentId}_${sessionId}")

                transaction.update(sessionRef, "currentStudentsCount", FieldValue.increment(-1))
                transaction.delete(bookingRef)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isAlreadyBooked(sessionId: String, studentId: String): Boolean {
        return try {
            val doc = bookingsCollection.document("${studentId}_${sessionId}").get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }
}