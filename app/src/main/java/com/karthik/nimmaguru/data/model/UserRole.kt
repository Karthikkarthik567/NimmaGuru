package com.karthik.nimmaguru.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class UserRole(val value: String) : Parcelable {
    STUDENT("student"),
    GURU("guru"),
    ADMIN("admin");

    companion object {
        fun fromString(value: String?): UserRole {
            return entries.find { it.value.equals(value, ignoreCase = true) } ?: STUDENT
        }
    }
}
