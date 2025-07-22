package com.timetableoptimizer.ai.ml

import android.content.Context
import com.timetableoptimizer.ai.data.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.math.exp

class PreferenceLearningModel(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private val inputSize = 15 // Number of features
    private val outputSize = 1 // Preference score
    
    data class SchedulingFeatures(
        val dayOfWeek: Int, // 1-7
        val hourOfDay: Int, // 0-23
        val subjectPriority: Int, // 1-4
        val subjectDifficulty: Int, // 1-4
        val isPreferredTimeSlot: Int, // 0 or 1
        val consecutiveHours: Int, // Number of consecutive study hours
        val totalDailyHours: Int, // Total hours scheduled for the day
        val breakDuration: Int, // Minutes since last session
        val energyLevel: Int, // Based on user's energy peak time
        val workloadBalance: Float, // 0.0-1.0
        val timeSlotWeight: Float, // Time slot preference weight
        val subjectFrequency: Int, // How often this subject appears in schedule
        val isWeekend: Int, // 0 or 1
        val sessionDuration: Int, // Duration in minutes
        val roomCapacity: Int // Room capacity if applicable
    )
    
    data class PreferencePrediction(
        val score: Float,
        val confidence: Float,
        val recommendation: String
    )
    
    suspend fun initializeModel(): Boolean = withContext(Dispatchers.IO) {
        try {
            // For this demo, we'll create a simple rule-based model
            // In a real implementation, you would load a pre-trained TensorFlow Lite model
            interpreter = createSimpleModel()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun createSimpleModel(): Interpreter {
        // Create a simple rule-based interpreter for demonstration
        // In production, this would load an actual TensorFlow Lite model
        return object : Interpreter(ByteBuffer.allocate(1)) {
            override fun run(input: Any, output: Any) {
                val inputArray = input as Array<FloatArray>
                val outputArray = output as Array<FloatArray>
                
                // Simple rule-based scoring
                val features = inputArray[0]
                var score = 0.5f // Base score
                
                // Day of week preference (weekdays preferred)
                if (features[0] in 1f..5f) score += 0.1f
                
                // Hour of day preference (9 AM to 5 PM preferred)
                val hour = features[1].toInt()
                if (hour in 9..17) score += 0.2f
                else if (hour in 7..8 || hour in 18..19) score += 0.1f
                else score -= 0.2f
                
                // Priority bonus
                score += features[2] * 0.1f
                
                // Difficulty penalty for late hours
                if (hour > 16) score -= features[3] * 0.05f
                
                // Preferred time slot bonus
                if (features[4] > 0) score += 0.15f
                
                // Consecutive hours penalty
                if (features[5] > 3) score -= 0.1f
                
                // Daily hours balance
                if (features[6] > 8) score -= 0.2f
                
                // Break duration bonus
                if (features[7] > 15) score += 0.1f
                
                // Energy level alignment
                score += features[8] * 0.05f
                
                // Workload balance bonus
                score += features[9] * 0.1f
                
                // Time slot weight
                score += features[10] * 0.1f
                
                // Weekend penalty
                if (features[12] > 0) score -= 0.1f
                
                // Clamp score between 0 and 1
                score = score.coerceIn(0f, 1f)
                
                outputArray[0][0] = score
            }
        }
    }
    
    suspend fun predictPreference(
        subject: Subject,
        timeSlot: TimeSlot,
        context: SchedulingContext
    ): PreferencePrediction = withContext(Dispatchers.Default) {
        
        val features = extractFeatures(subject, timeSlot, context)
        val inputArray = arrayOf(features.toFloatArray())
        val outputArray = arrayOf(floatArrayOf(0f))
        
        interpreter?.run(inputArray, outputArray)
        
        val score = outputArray[0][0]
        val confidence = calculateConfidence(features)
        val recommendation = generateRecommendation(score, features)
        
        PreferencePrediction(score, confidence, recommendation)
    }
    
    private fun extractFeatures(
        subject: Subject,
        timeSlot: TimeSlot,
        context: SchedulingContext
    ): List<Float> {
        return listOf(
            timeSlot.dayOfWeek.value.toFloat(),
            timeSlot.startTime.hour.toFloat(),
            subject.priority.value.toFloat(),
            subject.difficulty.value.toFloat(),
            if (timeSlot.isPreferred) 1f else 0f,
            context.consecutiveHours.toFloat(),
            context.totalDailyHours.toFloat(),
            context.breakDuration.toFloat(),
            context.energyLevel.toFloat(),
            context.workloadBalance,
            timeSlot.weight.toFloat(),
            context.subjectFrequency.toFloat(),
            if (timeSlot.dayOfWeek in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) 1f else 0f,
            timeSlot.duration.toFloat(),
            timeSlot.capacity?.toFloat() ?: 0f
        )
    }
    
    private fun calculateConfidence(features: List<Float>): Float {
        // Simple confidence calculation based on feature consistency
        var confidence = 0.8f
        
        // Reduce confidence for edge cases
        val hour = features[1].toInt()
        if (hour < 7 || hour > 20) confidence -= 0.2f
        
        val consecutiveHours = features[5].toInt()
        if (consecutiveHours > 4) confidence -= 0.1f
        
        val dailyHours = features[6].toInt()
        if (dailyHours > 10) confidence -= 0.2f
        
        return confidence.coerceIn(0f, 1f)
    }
    
    private fun generateRecommendation(score: Float, features: List<Float>): String {
        return when {
            score > 0.8f -> "Excellent time slot for this subject"
            score > 0.6f -> "Good time slot, recommended"
            score > 0.4f -> "Acceptable time slot"
            score > 0.2f -> "Not ideal, consider alternatives"
            else -> "Poor time slot, strongly recommend alternatives"
        }
    }
    
    data class SchedulingContext(
        val consecutiveHours: Int = 0,
        val totalDailyHours: Int = 0,
        val breakDuration: Int = 0,
        val energyLevel: Int = 3,
        val workloadBalance: Float = 0.5f,
        val subjectFrequency: Int = 0
    )
    
    suspend fun learnFromUserFeedback(
        subject: Subject,
        timeSlot: TimeSlot,
        userRating: Float, // 0.0 to 1.0
        context: SchedulingContext
    ) = withContext(Dispatchers.IO) {
        // In a real implementation, this would update the model weights
        // For now, we'll just log the feedback for future training
        val features = extractFeatures(subject, timeSlot, context)
        
        // Store feedback data for future model training
        // This could be saved to a local database or sent to a server
        println("User feedback: Rating=$userRating, Features=$features")
    }
    
    fun cleanup() {
        interpreter?.close()
        interpreter = null
    }
}
