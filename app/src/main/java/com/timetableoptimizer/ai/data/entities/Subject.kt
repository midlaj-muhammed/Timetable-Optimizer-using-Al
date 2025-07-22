package com.timetableoptimizer.ai.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey
    val id: String,
    val name: String,
    val code: String,
    val color: String,
    val credits: Int,
    val priority: Priority,
    val estimatedHoursPerWeek: Int,
    val preferredTimeSlots: List<String> = emptyList(), // JSON string of time slot IDs
    val difficulty: Difficulty,
    val instructor: String? = null,
    val room: String? = null,
    val description: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

enum class Priority(val value: Int, val displayName: String) {
    LOW(1, "Low"),
    MEDIUM(2, "Medium"),
    HIGH(3, "High"),
    CRITICAL(4, "Critical")
}

enum class Difficulty(val value: Int, val displayName: String) {
    EASY(1, "Easy"),
    MEDIUM(2, "Medium"),
    HARD(3, "Hard"),
    VERY_HARD(4, "Very Hard")
}
