package com.timetableoptimizer.ai.ui.screens.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timetableoptimizer.ai.R
import com.timetableoptimizer.ai.ui.components.TimetableGrid
import com.timetableoptimizer.ai.ui.components.WeeklyTimetableView
import com.timetableoptimizer.ai.ui.components.DailyTimetableView
import com.timetableoptimizer.ai.ui.viewmodels.TimetableDetailViewModel
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableDetailScreen(
    timetableId: String,
    onNavigateBack: () -> Unit,
    viewModel: TimetableDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedView by remember { mutableStateOf(TimetableView.WEEKLY) }
    var selectedDay by remember { mutableStateOf(DayOfWeek.MONDAY) }
    
    LaunchedEffect(timetableId) {
        viewModel.loadTimetable(timetableId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.timetable?.name ?: "Timetable",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.optimizeTimetable() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = stringResource(R.string.optimize_now)
                        )
                    }
                    
                    IconButton(
                        onClick = { /* Show export options */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.export)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TimetableInfoCard(
                        timetable = uiState.timetable,
                        optimizationScore = uiState.optimizationScore,
                        totalHours = uiState.totalHours,
                        subjectCount = uiState.subjectCount
                    )
                }
                
                item {
                    ViewToggleCard(
                        selectedView = selectedView,
                        selectedDay = selectedDay,
                        onViewChange = { selectedView = it },
                        onDayChange = { selectedDay = it }
                    )
                }
                
                item {
                    when (selectedView) {
                        TimetableView.WEEKLY -> {
                            WeeklyTimetableView(
                                entries = uiState.entries,
                                timeSlots = uiState.timeSlots,
                                subjects = uiState.subjects,
                                onEntryClick = { entry ->
                                    // Handle entry click
                                }
                            )
                        }
                        TimetableView.DAILY -> {
                            DailyTimetableView(
                                day = selectedDay,
                                entries = uiState.entries,
                                timeSlots = uiState.timeSlots,
                                subjects = uiState.subjects,
                                onEntryClick = { entry ->
                                    // Handle entry click
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Show optimization progress dialog
    if (uiState.isOptimizing) {
        OptimizationProgressDialog(
            progress = uiState.optimizationProgress,
            onDismiss = { /* Handle dismiss */ }
        )
    }
    
    // Show error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar
            viewModel.clearError()
        }
    }
}

@Composable
private fun TimetableInfoCard(
    timetable: Any?, // Replace with actual Timetable type
    optimizationScore: Double,
    totalHours: Int,
    subjectCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Schedule Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoItem(
                    value = "${(optimizationScore * 100).toInt()}%",
                    label = "Optimization",
                    icon = Icons.Default.TrendingUp
                )
                
                InfoItem(
                    value = "${totalHours}h",
                    label = "Total Hours",
                    icon = Icons.Default.Schedule
                )
                
                InfoItem(
                    value = subjectCount.toString(),
                    label = "Subjects",
                    icon = Icons.Default.Book
                )
            }
        }
    }
}

@Composable
private fun InfoItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun ViewToggleCard(
    selectedView: TimetableView,
    selectedDay: DayOfWeek,
    onViewChange: (TimetableView) -> Unit,
    onDayChange: (DayOfWeek) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "View Options",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { onViewChange(TimetableView.WEEKLY) },
                    label = { Text("Weekly") },
                    selected = selectedView == TimetableView.WEEKLY,
                    modifier = Modifier.weight(1f)
                )
                
                FilterChip(
                    onClick = { onViewChange(TimetableView.DAILY) },
                    label = { Text("Daily") },
                    selected = selectedView == TimetableView.DAILY,
                    modifier = Modifier.weight(1f)
                )
            }
            
            if (selectedView == TimetableView.DAILY) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DayOfWeek.values().forEach { day ->
                        FilterChip(
                            onClick = { onDayChange(day) },
                            label = { 
                                Text(
                                    text = day.name.take(3),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            selected = selectedDay == day,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OptimizationProgressDialog(
    progress: Float,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.optimizing))
        },
        text = {
            Column {
                Text(text = "Optimizing your timetable...")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

enum class TimetableView {
    WEEKLY, DAILY
}
