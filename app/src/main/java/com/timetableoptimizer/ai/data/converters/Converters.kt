package com.timetableoptimizer.ai.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timetableoptimizer.ai.data.entities.*
import java.time.DayOfWeek
import java.time.LocalTime

class Converters {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }
    
    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }
    
    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String {
        return difficulty.name
    }
    
    @TypeConverter
    fun toDifficulty(difficulty: String): Difficulty {
        return Difficulty.valueOf(difficulty)
    }
    
    @TypeConverter
    fun fromDayOfWeek(dayOfWeek: DayOfWeek): String {
        return dayOfWeek.name
    }
    
    @TypeConverter
    fun toDayOfWeek(dayOfWeek: String): DayOfWeek {
        return DayOfWeek.valueOf(dayOfWeek)
    }
    
    @TypeConverter
    fun fromLocalTime(time: LocalTime): String {
        return time.toString()
    }
    
    @TypeConverter
    fun toLocalTime(time: String): LocalTime {
        return LocalTime.parse(time)
    }
    
    @TypeConverter
    fun fromTimetableType(type: TimetableType): String {
        return type.name
    }
    
    @TypeConverter
    fun toTimetableType(type: String): TimetableType {
        return TimetableType.valueOf(type)
    }
    
    @TypeConverter
    fun fromTimetableStatus(status: TimetableStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toTimetableStatus(status: String): TimetableStatus {
        return TimetableStatus.valueOf(status)
    }
    
    @TypeConverter
    fun fromSessionType(type: SessionType): String {
        return type.name
    }
    
    @TypeConverter
    fun toSessionType(type: String): SessionType {
        return SessionType.valueOf(type)
    }
    
    @TypeConverter
    fun fromConstraintType(type: ConstraintType): String {
        return type.name
    }
    
    @TypeConverter
    fun toConstraintType(type: String): ConstraintType {
        return ConstraintType.valueOf(type)
    }
    
    @TypeConverter
    fun fromConstraintSeverity(severity: ConstraintSeverity): String {
        return severity.name
    }
    
    @TypeConverter
    fun toConstraintSeverity(severity: String): ConstraintSeverity {
        return ConstraintSeverity.valueOf(severity)
    }
    
    @TypeConverter
    fun fromStudyStyle(style: StudyStyle): String {
        return style.name
    }
    
    @TypeConverter
    fun toStudyStyle(style: String): StudyStyle {
        return StudyStyle.valueOf(style)
    }
    
    @TypeConverter
    fun fromEnergyLevel(level: EnergyLevel): String {
        return level.name
    }
    
    @TypeConverter
    fun toEnergyLevel(level: String): EnergyLevel {
        return EnergyLevel.valueOf(level)
    }
    
    @TypeConverter
    fun fromDifficultyDistribution(distribution: DifficultyDistribution): String {
        return distribution.name
    }
    
    @TypeConverter
    fun toDifficultyDistribution(distribution: String): DifficultyDistribution {
        return DifficultyDistribution.valueOf(distribution)
    }
}
