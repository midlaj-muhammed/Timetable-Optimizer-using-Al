package com.timetableoptimizer.ai.optimization

import com.timetableoptimizer.ai.data.entities.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.*

class TimetableOptimizerTest {
    
    private lateinit var optimizer: TimetableOptimizer
    private lateinit var testSubjects: List<Subject>
    private lateinit var testTimeSlots: List<TimeSlot>
    private lateinit var testConstraints: List<Constraint>
    private lateinit var testUserPreferences: UserPreferences
    
    @Before
    fun setup() {
        optimizer = TimetableOptimizer()
        
        testSubjects = listOf(
            Subject(
                id = "subject1",
                name = "Mathematics",
                code = "MATH101",
                color = "#FF3B30",
                credits = 3,
                priority = Priority.HIGH,
                estimatedHoursPerWeek = 4,
                difficulty = Difficulty.HARD
            ),
            Subject(
                id = "subject2",
                name = "Physics",
                code = "PHYS101",
                color = "#007AFF",
                credits = 3,
                priority = Priority.MEDIUM,
                estimatedHoursPerWeek = 3,
                difficulty = Difficulty.MEDIUM
            ),
            Subject(
                id = "subject3",
                name = "English",
                code = "ENG101",
                color = "#34C759",
                credits = 2,
                priority = Priority.LOW,
                estimatedHoursPerWeek = 2,
                difficulty = Difficulty.EASY
            )
        )
        
        testTimeSlots = listOf(
            TimeSlot(
                id = "slot1",
                dayOfWeek = DayOfWeek.MONDAY,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 0),
                isAvailable = true,
                isPreferred = true
            ),
            TimeSlot(
                id = "slot2",
                dayOfWeek = DayOfWeek.MONDAY,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                isAvailable = true,
                isPreferred = false
            ),
            TimeSlot(
                id = "slot3",
                dayOfWeek = DayOfWeek.TUESDAY,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 0),
                isAvailable = true,
                isPreferred = true
            )
        )
        
        testConstraints = listOf(
            Constraint(
                id = "constraint1",
                name = "No Time Conflicts",
                description = "Prevent scheduling multiple subjects at the same time",
                type = ConstraintType.TIME_CONFLICT,
                severity = ConstraintSeverity.HARD,
                parameters = "{}",
                weight = 1000.0,
                violationPenalty = 1000.0
            )
        )
        
        testUserPreferences = UserPreferences(
            preferredStartTime = LocalTime.of(8, 0),
            preferredEndTime = LocalTime.of(18, 0),
            maxHoursPerDay = 8,
            minBreakDuration = 15,
            maxConsecutiveHours = 3
        )
    }
    
    @Test
    fun `test optimization with valid input returns success`() = runBlocking {
        val input = TimetableOptimizer.OptimizationInput(
            subjects = testSubjects,
            timeSlots = testTimeSlots,
            constraints = testConstraints,
            userPreferences = testUserPreferences
        )
        
        val result = optimizer.optimizeTimetable(input, "test_timetable", 10)
        
        assertTrue("Optimization should succeed with valid input", result.success)
        assertTrue("Should have optimization score", result.score >= 0.0)
        assertNotNull("Should have entries", result.entries)
        assertEquals("Should have correct execution time", true, result.executionTimeMs > 0)
    }
    
    @Test
    fun `test optimization respects time conflicts`() = runBlocking {
        val input = TimetableOptimizer.OptimizationInput(
            subjects = testSubjects,
            timeSlots = testTimeSlots,
            constraints = testConstraints,
            userPreferences = testUserPreferences
        )
        
        val result = optimizer.optimizeTimetable(input, "test_timetable", 10)
        
        if (result.success) {
            // Check that no two subjects are scheduled at the same time slot
            val timeSlotIds = result.entries.map { it.timeSlotId }
            val uniqueTimeSlotIds = timeSlotIds.distinct()
            
            assertEquals(
                "No time conflicts should exist",
                timeSlotIds.size,
                uniqueTimeSlotIds.size
            )
        }
    }
    
    @Test
    fun `test optimization with no available time slots returns failure`() = runBlocking {
        val emptyTimeSlots = testTimeSlots.map { it.copy(isAvailable = false) }
        
        val input = TimetableOptimizer.OptimizationInput(
            subjects = testSubjects,
            timeSlots = emptyTimeSlots,
            constraints = testConstraints,
            userPreferences = testUserPreferences
        )
        
        val result = optimizer.optimizeTimetable(input, "test_timetable", 5)
        
        assertFalse("Optimization should fail with no available time slots", result.success)
        assertTrue("Should have empty entries list", result.entries.isEmpty())
    }
    
    @Test
    fun `test optimization prioritizes high priority subjects`() = runBlocking {
        val input = TimetableOptimizer.OptimizationInput(
            subjects = testSubjects,
            timeSlots = testTimeSlots,
            constraints = testConstraints,
            userPreferences = testUserPreferences
        )
        
        val result = optimizer.optimizeTimetable(input, "test_timetable", 10)
        
        if (result.success && result.entries.isNotEmpty()) {
            // Check if high priority subjects are scheduled
            val scheduledSubjectIds = result.entries.map { it.subjectId }
            val highPrioritySubject = testSubjects.find { it.priority == Priority.HIGH }
            
            if (highPrioritySubject != null) {
                assertTrue(
                    "High priority subject should be scheduled",
                    scheduledSubjectIds.contains(highPrioritySubject.id)
                )
            }
        }
    }
    
    @Test
    fun `test optimization score calculation`() = runBlocking {
        val input = TimetableOptimizer.OptimizationInput(
            subjects = testSubjects,
            timeSlots = testTimeSlots,
            constraints = testConstraints,
            userPreferences = testUserPreferences
        )
        
        val result = optimizer.optimizeTimetable(input, "test_timetable", 10)
        
        if (result.success) {
            assertTrue("Optimization score should be non-negative", result.score >= 0.0)
            
            // If there are violations, score should be reduced
            if (result.violations.isNotEmpty()) {
                val totalPenalty = result.violations.sumOf { it.penalty }
                assertTrue("Score should account for violations", totalPenalty > 0)
            }
        }
    }
    
    @Test
    fun `test optimization with existing entries`() = runBlocking {
        val existingEntries = listOf(
            TimetableEntry(
                id = "entry1",
                timetableId = "test_timetable",
                subjectId = "subject1",
                timeSlotId = "slot1",
                sessionType = SessionType.STUDY,
                duration = 60,
                isFixed = true
            )
        )
        
        val input = TimetableOptimizer.OptimizationInput(
            subjects = testSubjects,
            timeSlots = testTimeSlots,
            constraints = testConstraints,
            userPreferences = testUserPreferences,
            existingEntries = existingEntries
        )
        
        val result = optimizer.optimizeTimetable(input, "test_timetable", 10)
        
        if (result.success) {
            // Fixed entries should be preserved
            val fixedEntry = result.entries.find { it.id == "entry1" }
            assertNotNull("Fixed entry should be preserved", fixedEntry)
        }
    }
}
