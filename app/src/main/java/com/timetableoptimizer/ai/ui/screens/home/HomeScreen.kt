package com.timetableoptimizer.ai.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timetableoptimizer.ai.R
import com.timetableoptimizer.ai.ui.components.QuickActionCard
import com.timetableoptimizer.ai.ui.components.TimetableCard
import com.timetableoptimizer.ai.ui.theme.TimetableOptimizerAITheme
import com.timetableoptimizer.ai.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTimetables: () -> Unit,
    onNavigateToSubjects: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Welcome Section
        item {
            WelcomeSection()
        }
        
        // Quick Actions
        item {
            QuickActionsSection(
                onCreateTimetable = onNavigateToTimetables,
                onManageSubjects = onNavigateToSubjects,
                onViewAnalytics = onNavigateToAnalytics
            )
        }
        
        // Recent Timetables
        item {
            RecentTimetablesSection(
                timetables = uiState.recentTimetables,
                onTimetableClick = { /* Navigate to timetable detail */ }
            )
        }
        
        // Statistics Overview
        item {
            StatisticsSection(
                totalTimetables = uiState.totalTimetables,
                totalSubjects = uiState.totalSubjects,
                averageOptimizationScore = uiState.averageOptimizationScore
            )
        }
    }
}

@Composable
private fun WelcomeSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickActionsSection(
    onCreateTimetable: () -> Unit,
    onManageSubjects: () -> Unit,
    onViewAnalytics: () -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.quick_actions),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                QuickActionCard(
                    title = stringResource(R.string.create_new_timetable),
                    icon = Icons.Default.Add,
                    onClick = onCreateTimetable
                )
            }
            
            item {
                QuickActionCard(
                    title = stringResource(R.string.nav_subjects),
                    icon = Icons.Default.Book,
                    onClick = onManageSubjects
                )
            }
            
            item {
                QuickActionCard(
                    title = stringResource(R.string.view_analytics),
                    icon = Icons.Default.Analytics,
                    onClick = onViewAnalytics
                )
            }
        }
    }
}

@Composable
private fun RecentTimetablesSection(
    timetables: List<Any>, // Replace with actual Timetable type
    onTimetableClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.recent_timetables),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            TextButton(
                onClick = { /* Navigate to all timetables */ }
            ) {
                Text(text = "View All")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (timetables.isEmpty()) {
            EmptyStateCard(
                title = stringResource(R.string.no_timetables),
                description = stringResource(R.string.create_first_timetable),
                icon = Icons.Default.Schedule
            )
        } else {
            // Display recent timetables
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(timetables.take(3)) { timetable ->
                    TimetableCard(
                        timetable = timetable,
                        onClick = { onTimetableClick("") }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsSection(
    totalTimetables: Int,
    totalSubjects: Int,
    averageOptimizationScore: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Your Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    value = totalTimetables.toString(),
                    label = "Timetables",
                    icon = Icons.Default.Schedule
                )
                
                StatisticItem(
                    value = totalSubjects.toString(),
                    label = "Subjects",
                    icon = Icons.Default.Book
                )
                
                StatisticItem(
                    value = "${(averageOptimizationScore * 100).toInt()}%",
                    label = "Avg Score",
                    icon = Icons.Default.TrendingUp
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
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
private fun EmptyStateCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TimetableOptimizerAITheme {
        HomeScreen(
            onNavigateToTimetables = {},
            onNavigateToSubjects = {},
            onNavigateToAnalytics = {}
        )
    }
}
