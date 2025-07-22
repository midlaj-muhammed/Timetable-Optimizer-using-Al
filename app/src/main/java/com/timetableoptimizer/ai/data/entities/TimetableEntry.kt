package com.timetableoptimizer.ai.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "timetable_entries",
    foreignKeys = [
        ForeignKey(
            entity = Timetable::class,
            parentColumns = ["id"],
            childColumns = ["timetableId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TimeSlot::class,
            parentColumns = ["id"],
            childColumns = ["timeSlotId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["timetableId"]),
        Index(value = ["subjectId"]),
        Index(value = ["timeSlotId"]),
        Index(value = ["timetableId", "timeSlotId"], unique = true)
    ]
)
data class TimetableEntry(
    @PrimaryKey
    val id: String,
    val timetableId: String,
    val subjectId: String,
    val timeSlotId: String,
    val sessionType: SessionType,
    val duration: Int, // in minutes
    val location: String? = null,
    val instructor: String? = null,
    val notes: String? = null,
    val isFixed: Boolean = false, // Cannot be moved during optimization
    val weight: Double = 1.0, // Importance weight for optimization
    val color: String? = null, // Override subject color
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

enum class SessionType(val displayName: String) {
    LECTURE("Lecture"),
    TUTORIAL("Tutorial"),
    LAB("Laboratory"),
    SEMINAR("Seminar"),
    STUDY("Study Session"),
    REVIEW("Review Session"),
    EXAM("Exam"),
    BREAK("Break"),
    OTHER("Other")
}
