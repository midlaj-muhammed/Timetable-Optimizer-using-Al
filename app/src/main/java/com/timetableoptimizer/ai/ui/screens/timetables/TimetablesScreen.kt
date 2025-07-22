package com.timetableoptimizer.ai.ui.screens.timetables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timetableoptimizer.ai.R
import com.timetableoptimizer.ai.ui.components.TimetableCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetablesScreen(
    onNavigateToTimetableDetail: (String) -> Unit,
    onNavigateToCreateTimetable: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_timetables),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTimetable
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_new_timetable)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Placeholder for timetables list
            items(3) { index ->
                TimetableCard(
                    timetable = "Sample Timetable $index",
                    onClick = { onNavigateToTimetableDetail("sample_$index") }
                )
            }
        }
    }
}
