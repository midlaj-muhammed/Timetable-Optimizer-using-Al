package com.timetableoptimizer.ai.optimization

import com.timetableoptimizer.ai.data.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.chocosolver.solver.Model
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.Solution
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max

class TimetableOptimizer {
    
    data class OptimizationInput(
        val subjects: List<Subject>,
        val timeSlots: List<TimeSlot>,
        val constraints: List<Constraint>,
        val userPreferences: UserPreferences,
        val existingEntries: List<TimetableEntry> = emptyList()
    )
    
    data class OptimizationResult(
        val success: Boolean,
        val score: Double,
        val entries: List<TimetableEntry>,
        val violations: List<ConstraintViolation>,
        val executionTimeMs: Long,
        val message: String
    )
    
    data class ConstraintViolation(
        val constraintId: String,
        val constraintName: String,
        val severity: ConstraintSeverity,
        val penalty: Double,
        val description: String
    )
    
    suspend fun optimizeTimetable(
        input: OptimizationInput,
        timetableId: String,
        maxTimeoutSeconds: Int = 30
    ): OptimizationResult = withContext(Dispatchers.Default) {
        
        val startTime = System.currentTimeMillis()
        
        try {
            val model = Model("TimetableOptimization")
            
            // Create decision variables
            val subjectTimeSlotVars = createDecisionVariables(model, input)
            
            // Apply constraints
            applyConstraints(model, input, subjectTimeSlotVars)
            
            // Set objective function
            val objectiveVar = createObjectiveFunction(model, input, subjectTimeSlotVars)
            model.setObjective(Model.MAXIMIZE, objectiveVar)
            
            // Solve the model
            val solver = model.solver
            solver.limitTime("${maxTimeoutSeconds}s")
            
            val solution = solver.findOptimalSolution()
            val executionTime = System.currentTimeMillis() - startTime
            
            if (solution != null) {
                val entries = extractSolution(solution, input, subjectTimeSlotVars, timetableId)
                val violations = validateSolution(entries, input)
                val score = calculateScore(entries, input, violations)
                
                OptimizationResult(
                    success = true,
                    score = score,
                    entries = entries,
                    violations = violations,
                    executionTimeMs = executionTime,
                    message = "Optimization completed successfully"
                )
            } else {
                // Try to find any feasible solution
                val anySolution = solver.findSolution()
                if (anySolution != null) {
                    val entries = extractSolution(anySolution, input, subjectTimeSlotVars, timetableId)
                    val violations = validateSolution(entries, input)
                    val score = calculateScore(entries, input, violations)
                    
                    OptimizationResult(
                        success = true,
                        score = score,
                        entries = entries,
                        violations = violations,
                        executionTimeMs = executionTime,
                        message = "Found feasible solution (not optimal)"
                    )
                } else {
                    OptimizationResult(
                        success = false,
                        score = 0.0,
                        entries = emptyList(),
                        violations = emptyList(),
                        executionTimeMs = executionTime,
                        message = "No feasible solution found"
                    )
                }
            }
        } catch (e: Exception) {
            val executionTime = System.currentTimeMillis() - startTime
            OptimizationResult(
                success = false,
                score = 0.0,
                entries = emptyList(),
                violations = emptyList(),
                executionTimeMs = executionTime,
                message = "Optimization failed: ${e.message}"
            )
        }
    }
    
    private fun createDecisionVariables(
        model: Model,
        input: OptimizationInput
    ): Map<String, IntVar> {
        val variables = HashMap<String, IntVar>()
        
        // For each subject, create a variable representing which time slot it's assigned to
        // Value 0 means not assigned, values 1..n represent time slot indices
        for (subject in input.subjects) {
            val varName = "subject_${subject.id}"
            val variable = model.intVar(varName, 0, input.timeSlots.size)
            variables[subject.id] = variable
        }
        
        return variables
    }
    
    private fun applyConstraints(
        model: Model,
        input: OptimizationInput,
        subjectTimeSlotVars: Map<String, IntVar>
    ) {
        // Time conflict constraint - no two subjects at the same time slot
        val timeSlotAssignments = subjectTimeSlotVars.values.toTypedArray()
        model.allDifferentExcept0(timeSlotAssignments).post()
        
        // Subject-specific time slot preferences
        for (subject in input.subjects) {
            val subjectVar = subjectTimeSlotVars[subject.id]!!
            
            if (subject.preferredTimeSlots.isNotEmpty()) {
                val preferredIndices = subject.preferredTimeSlots.mapNotNull { timeSlotId ->
                    input.timeSlots.indexOfFirst { it.id == timeSlotId }.takeIf { it >= 0 }?.plus(1)
                }.toIntArray()
                
                if (preferredIndices.isNotEmpty()) {
                    // Add preference constraint (soft constraint through objective)
                    val preferredVar = model.intVar("preferred_${subject.id}", 0, 1)
                    model.ifThen(
                        model.member(subjectVar, preferredIndices),
                        model.arithm(preferredVar, "=", 1)
                    )
                }
            }
        }
        
        // Apply custom constraints
        for (constraint in input.constraints.filter { it.isActive }) {
            applyCustomConstraint(model, constraint, input, subjectTimeSlotVars)
        }
    }
    
    private fun applyCustomConstraint(
        model: Model,
        constraint: Constraint,
        input: OptimizationInput,
        subjectTimeSlotVars: Map<String, IntVar>
    ) {
        when (constraint.type) {
            ConstraintType.MAX_HOURS_PER_DAY -> {
                // Implementation for max hours per day constraint
                // This would require more complex modeling with day-wise variables
            }
            ConstraintType.MIN_BREAK_DURATION -> {
                // Implementation for minimum break duration
                // This would require temporal constraints between consecutive slots
            }
            ConstraintType.PREFERRED_TIME_SLOT -> {
                // Already handled in subject-specific preferences
            }
            else -> {
                // Handle other constraint types as needed
            }
        }
    }
    
    private fun createObjectiveFunction(
        model: Model,
        input: OptimizationInput,
        subjectTimeSlotVars: Map<String, IntVar>
    ): IntVar {
        val objectiveTerms = mutableListOf<IntVar>()
        
        // Priority-based scoring
        for (subject in input.subjects) {
            val subjectVar = subjectTimeSlotVars[subject.id]!!
            val priorityWeight = subject.priority.value * 10
            
            val assignedVar = model.intVar("assigned_${subject.id}", 0, 1)
            model.ifThen(
                model.arithm(subjectVar, ">", 0),
                model.arithm(assignedVar, "=", 1)
            )
            
            val weightedVar = model.intVar("weighted_${subject.id}", 0, priorityWeight)
            model.times(assignedVar, priorityWeight, weightedVar)
            objectiveTerms.add(weightedVar)
        }
        
        // Time slot preference scoring
        for ((index, timeSlot) in input.timeSlots.withIndex()) {
            if (timeSlot.isPreferred) {
                val slotIndex = index + 1
                val preferenceBonus = 5
                
                for (subject in input.subjects) {
                    val subjectVar = subjectTimeSlotVars[subject.id]!!
                    val bonusVar = model.intVar("bonus_${subject.id}_${timeSlot.id}", 0, preferenceBonus)
                    
                    model.ifThen(
                        model.arithm(subjectVar, "=", slotIndex),
                        model.arithm(bonusVar, "=", preferenceBonus)
                    )
                    objectiveTerms.add(bonusVar)
                }
            }
        }
        
        // Combine all objective terms
        val totalObjective = model.intVar("total_objective", 0, IntVar.MAX_INT_BOUND)
        model.sum(objectiveTerms.toTypedArray(), "=", totalObjective)
        
        return totalObjective
    }

    private fun extractSolution(
        solution: Solution,
        input: OptimizationInput,
        subjectTimeSlotVars: Map<String, IntVar>,
        timetableId: String
    ): List<TimetableEntry> {
        val entries = mutableListOf<TimetableEntry>()

        for (subject in input.subjects) {
            val subjectVar = subjectTimeSlotVars[subject.id]!!
            val timeSlotIndex = solution.getIntVal(subjectVar)

            if (timeSlotIndex > 0) {
                val timeSlot = input.timeSlots[timeSlotIndex - 1]
                val entry = TimetableEntry(
                    id = UUID.randomUUID().toString(),
                    timetableId = timetableId,
                    subjectId = subject.id,
                    timeSlotId = timeSlot.id,
                    sessionType = SessionType.STUDY,
                    duration = timeSlot.duration.toInt(),
                    weight = subject.priority.value.toDouble()
                )
                entries.add(entry)
            }
        }

        return entries
    }

    private fun validateSolution(
        entries: List<TimetableEntry>,
        input: OptimizationInput
    ): List<ConstraintViolation> {
        val violations = mutableListOf<ConstraintViolation>()

        // Check for time conflicts
        val timeSlotGroups = entries.groupBy { it.timeSlotId }
        for ((timeSlotId, entriesInSlot) in timeSlotGroups) {
            if (entriesInSlot.size > 1) {
                violations.add(
                    ConstraintViolation(
                        constraintId = "time_conflict",
                        constraintName = "Time Conflict",
                        severity = ConstraintSeverity.HARD,
                        penalty = 1000.0,
                        description = "Multiple subjects scheduled at the same time slot: $timeSlotId"
                    )
                )
            }
        }

        // Validate against custom constraints
        for (constraint in input.constraints.filter { it.isActive }) {
            val constraintViolations = validateConstraint(constraint, entries, input)
            violations.addAll(constraintViolations)
        }

        return violations
    }

    private fun validateConstraint(
        constraint: Constraint,
        entries: List<TimetableEntry>,
        input: OptimizationInput
    ): List<ConstraintViolation> {
        val violations = mutableListOf<ConstraintViolation>()

        when (constraint.type) {
            ConstraintType.MAX_HOURS_PER_DAY -> {
                // Group entries by day and check total duration
                val entriesByDay = entries.groupBy { entry ->
                    input.timeSlots.find { it.id == entry.timeSlotId }?.dayOfWeek
                }

                for ((day, dayEntries) in entriesByDay) {
                    val totalHours = dayEntries.sumOf { it.duration } / 60.0
                    if (totalHours > input.userPreferences.maxHoursPerDay) {
                        violations.add(
                            ConstraintViolation(
                                constraintId = constraint.id,
                                constraintName = constraint.name,
                                severity = constraint.severity,
                                penalty = constraint.violationPenalty,
                                description = "Exceeded max hours per day on $day: $totalHours hours"
                            )
                        )
                    }
                }
            }
            else -> {
                // Handle other constraint types
            }
        }

        return violations
    }

    private fun calculateScore(
        entries: List<TimetableEntry>,
        input: OptimizationInput,
        violations: List<ConstraintViolation>
    ): Double {
        var score = 0.0

        // Base score from assigned subjects
        for (entry in entries) {
            val subject = input.subjects.find { it.id == entry.subjectId }
            if (subject != null) {
                score += subject.priority.value * 10.0

                // Bonus for preferred time slots
                val timeSlot = input.timeSlots.find { it.id == entry.timeSlotId }
                if (timeSlot?.isPreferred == true) {
                    score += 5.0
                }
            }
        }

        // Subtract penalties for violations
        for (violation in violations) {
            score -= violation.penalty
        }

        return max(0.0, score)
    }
}
