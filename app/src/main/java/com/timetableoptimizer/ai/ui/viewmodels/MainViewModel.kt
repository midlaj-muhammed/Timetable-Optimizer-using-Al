package com.timetableoptimizer.ai.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timetableoptimizer.ai.TimetableOptimizerApplication
import com.timetableoptimizer.ai.data.entities.Timetable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val isLoading: Boolean = false,
    val currentTimetable: Timetable? = null,
    val error: String? = null
)

class MainViewModel : ViewModel() {
    
    private val database = TimetableOptimizerApplication.instance.database
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        loadCurrentTimetable()
    }
    
    private fun loadCurrentTimetable() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val currentTimetable = database.timetableDao().getCurrentActiveTimetable()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentTimetable = currentTimetable
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
