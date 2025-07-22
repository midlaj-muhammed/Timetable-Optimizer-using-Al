package com.timetableoptimizer.ai.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalTime

@Parcelize
@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey
    val id: String = "default",
    val preferredStartTime: LocalTime = LocalTime.of(8, 0),
    val preferredEndTime: LocalTime = LocalTime.of(18, 0),
    val maxHoursPerDay: Int = 8,
    val minBreakDuration: Int = 15, // minutes
    val maxConsecutiveHours: Int = 3,
    val preferredBreakTime: LocalTime = LocalTime.of(12, 0),
    val lunchBreakDuration: Int = 60, // minutes
    val studyStyle: StudyStyle = StudyStyle.BALANCED,
    val energyPeakTime: EnergyLevel = EnergyLevel.MORNING,
    val difficultyDistribution: DifficultyDistribution = DifficultyDistribution.MIXED,
    val allowWeekends: Boolean = false,
    val allowEvenings: Boolean = true,
    val prioritizeConsistency: Boolean = true,
    val minimizeGaps: Boolean = true,
    val balanceWorkload: Boolean = true,
    val optimizationWeights: String = "", // JSON string of optimization weights
    val notifications: Boolean = true,
    val reminderMinutes: Int = 15,
    val theme: String = "system", // light, dark, system
    val language: String = "en",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

enum class StudyStyle(val displayName: String) {
    INTENSIVE("Intensive"),
    BALANCED("Balanced"),
    RELAXED("Relaxed"),
    FLEXIBLE("Flexible")
}

enum class EnergyLevel(val displayName: String) {
    MORNING("Morning Person"),
    AFTERNOON("Afternoon Person"),
    EVENING("Evening Person"),
    NIGHT("Night Owl")
}

enum class DifficultyDistribution(val displayName: String) {
    FRONT_LOADED("Front-loaded"),
    BACK_LOADED("Back-loaded"),
    MIXED("Mixed"),
    EVEN("Even Distribution")
}
