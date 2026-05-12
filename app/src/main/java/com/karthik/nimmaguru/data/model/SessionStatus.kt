package com.karthik.nimmaguru.data.model

/**
 * Nimma-Guru Session Status
 * Defines the lifecycle of a session at the Samudaya Bhavana.
 */
enum class SessionStatus(val value: String) {
    UPCOMING("upcoming"),
    ONGOING("ongoing"),
    COMPLETED("completed"),
    CANCELLED("cancelled");

    companion object {
        fun fromString(value: String): SessionStatus {
            return entries.find { it.value.equals(value, ignoreCase = true) } ?: UPCOMING
        }
    }
}
