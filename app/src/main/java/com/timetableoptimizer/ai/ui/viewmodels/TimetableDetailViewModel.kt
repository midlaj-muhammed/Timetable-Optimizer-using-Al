package com.timetableoptimizer.ai.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timetableoptimizer.ai.TimetableOptimizerApplication
import com.timetableoptimizer.ai.data.entities.*
import com.timetableoptimizer.ai.optimization.TimetableOptimizer
import com.timetableoptimizer.ai.ml.PreferenceLearningModel
import com.timetableoptimizer.ai.ml.SchedulingSuggestionEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class TimetableDetailUiState(
    val isLoading: Boolean = false,
    val isOptimizing: Boolean = false,
    val optimizationProgress: Float = 0f,
    val timetable: Timetable? = null,
    val entries: List<TimetableEntry> = emptyList(),
    val timeSlots: List<TimeSlot> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val constraints: List<Constraint> = emptyList(),
    val userPreferences: UserPreferences? = null,
    val optimizationScore: Double = 0.0,
    val totalHours: Int = 0,
    val subjectCount: Int = 0,
    val error: String? = null
)

class TimetableDetailViewModel : ViewModel() {
    
    private val database = TimetableOptimizerApplication.instance.database
    private val optimizer = TimetableOptimizer()
    private val preferenceLearningModel = PreferenceLearningModel(TimetableOptimizerApplication.instance)
    private val suggestionEngine = SchedulingSuggestionEngine(preferenceLearningModel)
    
    private val _uiState = MutableStateFlow(TimetableDetailUiState())
    val uiState: StateFlow<TimetableDetailUiState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            preferenceLearningModel.initializeModel()
        }
    }
    
    fun loadTimetable(timetableId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Load timetable
                val timetable = database.timetableDao().getTimetableById(timetableId)
                
                if (timetable != null) {
                    // Load related data
                    val entries = database.timetableEntryDao().getEntriesByTimetableIdSync(timetableId)
                    val timeSlots = database.timeSlotDao().getAllTimeSlots().first()
                    val subjects = database.subjectDao().getAllActiveSubjects().first()
                    val constraints = database.constraintDao().getAllActiveConstraints().first()
                    val userPreferences = database.userPreferencesDao().getUserPreferencesSync()
                    
                    // Calculate statistics
                    val totalHours = entries.sumOf { it.duration } / 60
                    val subjectCount = entries.map { it.subjectId }.distinct().size
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        timetable = timetable,
                        entries = entries,
                        timeSlots = timeSlots,
                        subjects = subjects,
                        constraints = constraints,
                        userPreferences = userPreferences,
                        optimizationScore = timetable.optimizationScore,
                        totalHours = totalHours,
                        subjectCount = subjectCount
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Timetable not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun optimizeTimetable() {
        val currentState = _uiState.value
        val timetable = currentState.timetable ?: return
        val userPreferences = currentState.userPreferences ?: return
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isOptimizing = true,
                    optimizationProgress = 0f
                )
                
                // Update timetable status
                database.timetableDao().updateTimetableStatus(
                    timetable.id,
                    TimetableStatus.OPTIMIZING
                )
                
                // Prepare optimization input
                val optimizationInput = TimetableOptimizer.OptimizationInput(
                    subjects = currentState.subjects,
                    timeSlots = currentState.timeSlots,
                    constraints = currentState.constraints,
                    userPreferences = userPreferences,
                    existingEntries = currentState.entries
                )
                
                // Simulate progress updates
                for (i in 1..5) {
                    _uiState.value = _uiState.value.copy(
                        optimizationProgress = i * 0.2f
                    )
                    kotlinx.coroutines.delay(500)
                }
                
                // Run optimization
                val result = optimizer.optimizeTimetable(
                    input = optimizationInput,
                    timetableId = timetable.id,
                    maxTimeoutSeconds = 30
                )
                
                if (result.success) {
                    // Clear existing entries
                    database.timetableEntryDao().deleteEntriesByTimetableId(timetable.id)
                    
                    // Insert optimized entries
                    database.timetableEntryDao().insertEntries(result.entries)
                    
                    // Update timetable with optimization results
                    val updatedTimetable = timetable.copy(
                        optimizationScore = result.score,
                        status = TimetableStatus.OPTIMIZED,
                        lastOptimizedAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    database.timetableDao().updateTimetable(updatedTimetable)
                    
                    // Reload data
                    loadTimetable(timetable.id)
                    
                    _uiState.value = _uiState.value.copy(
                        isOptimizing = false,
                        optimizationProgress = 1f
                    )
                } else {
                    // Update timetable status to failed
                    database.timetableDao().updateTimetableStatus(
                        timetable.id,
                        TimetableStatus.FAILED
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        isOptimizing = false,
                        error = result.message
                    )
                }
            } catch (e: Exception) {
                // Update timetable status to failed
                database.timetableDao().updateTimetableStatus(
                    timetable.id,
                    TimetableStatus.FAILED
                )
                
                _uiState.value = _uiState.value.copy(
                    isOptimizing = false,
                    error = e.message
                )
            }
        }
    }
    
    fun generateSuggestions() {
        val currentState = _uiState.value
        val userPreferences = currentState.userPreferences ?: return
        
        viewModelScope.launch {
            try {
                val suggestions = suggestionEngine.generateSchedulingSuggestions(
                    subjects = currentState.subjects,
                    availableTimeSlots = currentState.timeSlots.filter { it.isAvailable },
                    userPreferences = userPreferences,
                    existingEntries = currentState.entries
                )
                
                // Handle suggestions (could be displayed in a dialog or separate screen)
                // For now, just log them
                suggestions.forEach { suggestion ->
                    println("Suggestion: ${suggestion.subject.name} at ${suggestion.timeSlot.displayName} (Score: ${suggestion.score})")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun addEntry(subjectId: String, timeSlotId: String) {
        val timetable = _uiState.value.timetable ?: return
        
        viewModelScope.launch {
            try {
                val newEntry = TimetableEntry(
                    id = UUID.randomUUID().toString(),
                    timetableId = timetable.id,
                    subjectId = subjectId,
                    timeSlotId = timeSlotId,
                    sessionType = SessionType.STUDY,
                    duration = 60 // Default 1 hour
                )
                
                database.timetableEntryDao().insertEntry(newEntry)
                loadTimetable(timetable.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun removeEntry(entryId: String) {
        val timetable = _uiState.value.timetable ?: return
        
        viewModelScope.launch {
            try {
                database.timetableEntryDao().deleteEntryById(entryId)
                loadTimetable(timetable.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun updateEntry(entry: TimetableEntry) {
        val timetable = _uiState.value.timetable ?: return
        
        viewModelScope.launch {
            try {
                database.timetableEntryDao().updateEntry(
                    entry.copy(updatedAt = System.currentTimeMillis())
                )
                loadTimetable(timetable.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        preferenceLearningModel.cleanup()
    }
}
