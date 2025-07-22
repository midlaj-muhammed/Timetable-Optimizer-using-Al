package com.timetableoptimizer.ai.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "constraints")
data class Constraint(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val type: ConstraintType,
    val severity: ConstraintSeverity,
    val isActive: Boolean = true,
    val parameters: String, // JSON string of constraint parameters
    val weight: Double = 1.0,
    val violationPenalty: Double = 100.0,
    val applicableSubjects: List<String> = emptyList(), // Subject IDs
    val applicableTimeSlots: List<String> = emptyList(), // TimeSlot IDs
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

enum class ConstraintType(val displayName: String) {
    TIME_CONFLICT("Time Conflict"),
    ROOM_CAPACITY("Room Capacity"),
    INSTRUCTOR_AVAILABILITY("Instructor Availability"),
    SUBJECT_PREREQUISITE("Subject Prerequisite"),
    MAX_HOURS_PER_DAY("Max Hours Per Day"),
    MIN_BREAK_DURATION("Minimum Break Duration"),
    CONSECUTIVE_SESSIONS("Consecutive Sessions"),
    PREFERRED_TIME_SLOT("Preferred Time Slot"),
    AVOID_TIME_SLOT("Avoid Time Slot"),
    SAME_DAY_SUBJECTS("Same Day Subjects"),
    DIFFERENT_DAY_SUBJECTS("Different Day Subjects"),
    WORKLOAD_BALANCE("Workload Balance"),
    CUSTOM("Custom Constraint")
}

enum class ConstraintSeverity(val displayName: String, val weight: Double) {
    HARD(displayName = "Hard", weight = 1000.0), // Must be satisfied
    SOFT(displayName = "Soft", weight = 100.0),  // Should be satisfied
    PREFERENCE(displayName = "Preference", weight = 10.0) // Nice to have
}
