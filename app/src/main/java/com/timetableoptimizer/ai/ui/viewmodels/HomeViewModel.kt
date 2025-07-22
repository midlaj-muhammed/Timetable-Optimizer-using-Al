package com.timetableoptimizer.ai.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timetableoptimizer.ai.TimetableOptimizerApplication
import com.timetableoptimizer.ai.data.entities.Timetable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val recentTimetables: List<Timetable> = emptyList(),
    val totalTimetables: Int = 0,
    val totalSubjects: Int = 0,
    val averageOptimizationScore: Double = 0.0,
    val error: String? = null
)

class HomeViewModel : ViewModel() {
    
    private val database = TimetableOptimizerApplication.instance.database
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Load recent timetables
                database.timetableDao().getActiveTimetables()
                    .take(1)
                    .collect { timetables ->
                        val recentTimetables = timetables.take(3)
                        val totalTimetables = timetables.size
                        val averageScore = if (timetables.isNotEmpty()) {
                            timetables.map { it.optimizationScore }.average()
                        } else 0.0
                        
                        _uiState.value = _uiState.value.copy(
                            recentTimetables = recentTimetables,
                            totalTimetables = totalTimetables,
                            averageOptimizationScore = averageScore
                        )
                    }
                
                // Load total subjects count
                val totalSubjects = database.subjectDao().getActiveSubjectCount()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    totalSubjects = totalSubjects
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun refreshData() {
        loadHomeData()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
