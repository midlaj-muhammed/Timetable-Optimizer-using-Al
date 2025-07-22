package com.timetableoptimizer.ai.ml

import com.timetableoptimizer.ai.data.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow

class SchedulingSuggestionEngine(
    private val preferenceLearningModel: PreferenceLearningModel
) {
    
    data class SchedulingSuggestion(
        val subject: Subject,
        val timeSlot: TimeSlot,
        val score: Float,
        val confidence: Float,
        val reasoning: List<String>,
        val alternatives: List<TimeSlot> = emptyList()
    )
    
    data class OptimizationSuggestion(
        val type: SuggestionType,
        val title: String,
        val description: String,
        val impact: ImpactLevel,
        val actionRequired: String,
        val affectedSubjects: List<String> = emptyList()
    )
    
    enum class SuggestionType {
        TIME_REDISTRIBUTION,
        BREAK_OPTIMIZATION,
        WORKLOAD_BALANCE,
        ENERGY_ALIGNMENT,
        CONFLICT_RESOLUTION,
        EFFICIENCY_IMPROVEMENT
    }
    
    enum class ImpactLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    suspend fun generateSchedulingSuggestions(
        subjects: List<Subject>,
        availableTimeSlots: List<TimeSlot>,
        userPreferences: UserPreferences,
        existingEntries: List<TimetableEntry> = emptyList()
    ): List<SchedulingSuggestion> = withContext(Dispatchers.Default) {
        
        val suggestions = mutableListOf<SchedulingSuggestion>()
        
        for (subject in subjects) {
            val bestSlots = findBestTimeSlotsForSubject(
                subject, availableTimeSlots, userPreferences, existingEntries
            )
            
            for ((timeSlot, score, reasoning) in bestSlots.take(3)) {
                val context = createSchedulingContext(timeSlot, existingEntries, userPreferences)
                val prediction = preferenceLearningModel.predictPreference(subject, timeSlot, context)
                
                val alternatives = bestSlots.drop(1).take(2).map { it.first }
                
                suggestions.add(
                    SchedulingSuggestion(
                        subject = subject,
                        timeSlot = timeSlot,
                        score = (score + prediction.score) / 2,
                        confidence = prediction.confidence,
                        reasoning = reasoning,
                        alternatives = alternatives
                    )
                )
            }
        }
        
        suggestions.sortedByDescending { it.score }
    }
    
    private data class ScoredTimeSlot(
        val first: TimeSlot,
        val second: Float,
        val third: List<String>
    )
    
    private fun findBestTimeSlotsForSubject(
        subject: Subject,
        availableTimeSlots: List<TimeSlot>,
        userPreferences: UserPreferences,
        existingEntries: List<TimetableEntry>
    ): List<ScoredTimeSlot> {
        
        val scoredSlots = mutableListOf<ScoredTimeSlot>()
        
        for (timeSlot in availableTimeSlots.filter { it.isAvailable }) {
            val score = calculateTimeSlotScore(subject, timeSlot, userPreferences, existingEntries)
            val reasoning = generateReasoning(subject, timeSlot, userPreferences, score)
            
            scoredSlots.add(ScoredTimeSlot(timeSlot, score, reasoning))
        }
        
        return scoredSlots.sortedByDescending { it.second }
    }
    
    private fun calculateTimeSlotScore(
        subject: Subject,
        timeSlot: TimeSlot,
        userPreferences: UserPreferences,
        existingEntries: List<TimetableEntry>
    ): Float {
        var score = 0.5f // Base score
        
        // Priority alignment
        score += subject.priority.value * 0.1f
        
        // Time preference alignment
        if (timeSlot.startTime >= userPreferences.preferredStartTime &&
            timeSlot.endTime <= userPreferences.preferredEndTime) {
            score += 0.2f
        }
        
        // Energy level alignment
        val energyScore = calculateEnergyAlignment(timeSlot, userPreferences.energyPeakTime)
        score += energyScore * 0.15f
        
        // Difficulty-time alignment
        val difficultyScore = calculateDifficultyTimeAlignment(subject, timeSlot)
        score += difficultyScore * 0.1f
        
        // Workload balance
        val workloadScore = calculateWorkloadBalance(timeSlot, existingEntries)
        score += workloadScore * 0.1f
        
        // Break optimization
        val breakScore = calculateBreakOptimization(timeSlot, existingEntries, userPreferences)
        score += breakScore * 0.1f
        
        // Preferred time slot bonus
        if (timeSlot.isPreferred) score += 0.1f
        
        // Weekend penalty (if not allowed)
        if (!userPreferences.allowWeekends && 
            timeSlot.dayOfWeek in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) {
            score -= 0.3f
        }
        
        // Evening penalty (if not allowed)
        if (!userPreferences.allowEvenings && timeSlot.startTime.hour >= 18) {
            score -= 0.2f
        }
        
        return score.coerceIn(0f, 1f)
    }
    
    private fun calculateEnergyAlignment(timeSlot: TimeSlot, energyPeakTime: EnergyLevel): Float {
        val hour = timeSlot.startTime.hour
        
        return when (energyPeakTime) {
            EnergyLevel.MORNING -> when (hour) {
                in 6..10 -> 1.0f
                in 11..12 -> 0.7f
                in 13..15 -> 0.5f
                else -> 0.3f
            }
            EnergyLevel.AFTERNOON -> when (hour) {
                in 12..16 -> 1.0f
                in 10..11, 17..18 -> 0.7f
                in 8..9, 19..20 -> 0.5f
                else -> 0.3f
            }
            EnergyLevel.EVENING -> when (hour) {
                in 17..21 -> 1.0f
                in 15..16, 22..23 -> 0.7f
                in 13..14 -> 0.5f
                else -> 0.3f
            }
            EnergyLevel.NIGHT -> when (hour) {
                in 22..23, 0..2 -> 1.0f
                in 20..21, 3..5 -> 0.7f
                in 18..19 -> 0.5f
                else -> 0.3f
            }
        }
    }
    
    private fun calculateDifficultyTimeAlignment(subject: Subject, timeSlot: TimeSlot): Float {
        val hour = timeSlot.startTime.hour
        
        return when (subject.difficulty) {
            Difficulty.VERY_HARD -> when (hour) {
                in 9..12 -> 1.0f // Best focus time
                in 14..16 -> 0.8f
                in 8..8, 13..13, 17..18 -> 0.6f
                else -> 0.3f
            }
            Difficulty.HARD -> when (hour) {
                in 9..16 -> 0.9f
                in 8..8, 17..18 -> 0.7f
                else -> 0.5f
            }
            Difficulty.MEDIUM -> when (hour) {
                in 8..18 -> 0.8f
                else -> 0.6f
            }
            Difficulty.EASY -> 0.7f // Can be done anytime
        }
    }
    
    private fun calculateWorkloadBalance(
        timeSlot: TimeSlot,
        existingEntries: List<TimetableEntry>
    ): Float {
        // Count existing entries on the same day
        val dayEntries = existingEntries.count { entry ->
            // This would need to be implemented with actual time slot lookup
            // For now, return a simple balance score
            false
        }
        
        return when (dayEntries) {
            0 -> 1.0f
            1, 2 -> 0.8f
            3, 4 -> 0.6f
            5, 6 -> 0.4f
            else -> 0.2f
        }
    }
    
    private fun calculateBreakOptimization(
        timeSlot: TimeSlot,
        existingEntries: List<TimetableEntry>,
        userPreferences: UserPreferences
    ): Float {
        // Simple break optimization - prefer slots with adequate breaks
        // This would need more sophisticated implementation with actual scheduling logic
        return 0.7f
    }
    
    private fun generateReasoning(
        subject: Subject,
        timeSlot: TimeSlot,
        userPreferences: UserPreferences,
        score: Float
    ): List<String> {
        val reasons = mutableListOf<String>()
        
        if (score > 0.8f) {
            reasons.add("Excellent match for your preferences")
        }
        
        if (timeSlot.isPreferred) {
            reasons.add("This is one of your preferred time slots")
        }
        
        if (timeSlot.startTime >= userPreferences.preferredStartTime &&
            timeSlot.endTime <= userPreferences.preferredEndTime) {
            reasons.add("Falls within your preferred study hours")
        }
        
        val energyAlignment = calculateEnergyAlignment(timeSlot, userPreferences.energyPeakTime)
        if (energyAlignment > 0.8f) {
            reasons.add("Aligns well with your energy peak time")
        }
        
        if (subject.priority == Priority.HIGH || subject.priority == Priority.CRITICAL) {
            reasons.add("High priority subject deserves prime time slot")
        }
        
        if (subject.difficulty == Difficulty.VERY_HARD && timeSlot.startTime.hour in 9..12) {
            reasons.add("Difficult subject scheduled during peak focus hours")
        }
        
        if (reasons.isEmpty()) {
            reasons.add("Reasonable time slot for this subject")
        }
        
        return reasons
    }
    
    private fun createSchedulingContext(
        timeSlot: TimeSlot,
        existingEntries: List<TimetableEntry>,
        userPreferences: UserPreferences
    ): PreferenceLearningModel.SchedulingContext {
        // This would calculate actual context from existing entries
        // For now, return default context
        return PreferenceLearningModel.SchedulingContext()
    }
}
