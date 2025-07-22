package com.timetableoptimizer.ai.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "timetables")
data class Timetable(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String? = null,
    val type: TimetableType,
    val status: TimetableStatus,
    val optimizationScore: Double = 0.0,
    val totalHours: Int = 0,
    val startDate: Long, // Timestamp
    val endDate: Long, // Timestamp
    val isTemplate: Boolean = false,
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList(), // JSON string of tags
    val settings: String? = null, // JSON string of optimization settings
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastOptimizedAt: Long? = null
) : Parcelable

enum class TimetableType(val displayName: String) {
    STUDY("Study Schedule"),
    CLASS("Class Schedule"),
    EXAM("Exam Schedule"),
    PERSONAL("Personal Schedule"),
    MIXED("Mixed Schedule")
}

enum class TimetableStatus(val displayName: String) {
    DRAFT("Draft"),
    OPTIMIZING("Optimizing"),
    OPTIMIZED("Optimized"),
    ACTIVE("Active"),
    ARCHIVED("Archived"),
    FAILED("Failed")
}
