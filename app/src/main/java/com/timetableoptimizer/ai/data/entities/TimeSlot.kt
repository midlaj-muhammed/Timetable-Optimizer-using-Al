package com.timetableoptimizer.ai.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.DayOfWeek
import java.time.LocalTime

@Parcelize
@Entity(tableName = "time_slots")
data class TimeSlot(
    @PrimaryKey
    val id: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isAvailable: Boolean = true,
    val isPreferred: Boolean = false,
    val weight: Double = 1.0, // For optimization scoring
    val room: String? = null,
    val capacity: Int? = null,
    val equipment: List<String> = emptyList(), // JSON string of equipment
    val notes: String? = null
) : Parcelable {
    
    val duration: Long
        get() = java.time.Duration.between(startTime, endTime).toMinutes()
    
    val displayName: String
        get() = "${dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }} ${startTime}-${endTime}"
    
    fun overlaps(other: TimeSlot): Boolean {
        return dayOfWeek == other.dayOfWeek &&
                !(endTime <= other.startTime || startTime >= other.endTime)
    }
    
    fun isWithinTimeRange(start: LocalTime, end: LocalTime): Boolean {
        return startTime >= start && endTime <= end
    }
}
