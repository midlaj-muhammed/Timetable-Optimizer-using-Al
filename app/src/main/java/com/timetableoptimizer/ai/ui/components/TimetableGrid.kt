package com.timetableoptimizer.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timetableoptimizer.ai.data.entities.TimetableEntry
import com.timetableoptimizer.ai.data.entities.TimeSlot
import com.timetableoptimizer.ai.data.entities.Subject
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TimetableGrid(
    entries: List<TimetableEntry>,
    timeSlots: List<TimeSlot>,
    subjects: List<Subject>,
    onEntryClick: (TimetableEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )
    
    val timeSlotsByDay = timeSlots.groupBy { it.dayOfWeek }
    val entriesByTimeSlot = entries.associateBy { it.timeSlotId }
    val subjectsById = subjects.associateBy { it.id }
    
    // Get unique time slots for the time column
    val uniqueTimeSlots = timeSlots
        .distinctBy { "${it.startTime}-${it.endTime}" }
        .sortedBy { it.startTime }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with days
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Time column header
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Time",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Day headers
                days.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.name.take(3),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            
            // Time slots and entries
            LazyColumn {
                items(uniqueTimeSlots) { timeSlot ->
                    TimetableRow(
                        timeSlot = timeSlot,
                        days = days,
                        timeSlotsByDay = timeSlotsByDay,
                        entriesByTimeSlot = entriesByTimeSlot,
                        subjectsById = subjectsById,
                        onEntryClick = onEntryClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimetableRow(
    timeSlot: TimeSlot,
    days: List<DayOfWeek>,
    timeSlotsByDay: Map<DayOfWeek, List<TimeSlot>>,
    entriesByTimeSlot: Map<String, TimetableEntry>,
    subjectsById: Map<String, Subject>,
    onEntryClick: (TimetableEntry) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        // Time column
        Box(
            modifier = Modifier
                .width(80.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = timeSlot.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = timeSlot.endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Day columns
        days.forEach { day ->
            val dayTimeSlots = timeSlotsByDay[day] ?: emptyList()
            val matchingTimeSlot = dayTimeSlots.find { 
                it.startTime == timeSlot.startTime && it.endTime == timeSlot.endTime 
            }
            val entry = matchingTimeSlot?.let { entriesByTimeSlot[it.id] }
            val subject = entry?.let { subjectsById[it.subjectId] }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                    .padding(2.dp)
            ) {
                if (entry != null && subject != null) {
                    Card(
                        onClick = { onEntryClick(entry) },
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(android.graphics.Color.parseColor(subject.color))
                                .copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = subject.code,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 9.sp
                            )
                            
                            if (entry.location != null) {
                                Text(
                                    text = entry.location,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyTimetableView(
    entries: List<TimetableEntry>,
    timeSlots: List<TimeSlot>,
    subjects: List<Subject>,
    onEntryClick: (TimetableEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Weekly Schedule",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        item {
            TimetableGrid(
                entries = entries,
                timeSlots = timeSlots,
                subjects = subjects,
                onEntryClick = onEntryClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun DailyTimetableView(
    day: DayOfWeek,
    entries: List<TimetableEntry>,
    timeSlots: List<TimeSlot>,
    subjects: List<Subject>,
    onEntryClick: (TimetableEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayTimeSlots = timeSlots.filter { it.dayOfWeek == day }.sortedBy { it.startTime }
    val entriesByTimeSlot = entries.associateBy { it.timeSlotId }
    val subjectsById = subjects.associateBy { it.id }
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "${day.name.lowercase().replaceFirstChar { it.uppercase() }} Schedule",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        items(dayTimeSlots) { timeSlot ->
            val entry = entriesByTimeSlot[timeSlot.id]
            val subject = entry?.let { subjectsById[it.subjectId] }
            
            DailyTimeSlotCard(
                timeSlot = timeSlot,
                entry = entry,
                subject = subject,
                onEntryClick = onEntryClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyTimeSlotCard(
    timeSlot: TimeSlot,
    entry: TimetableEntry?,
    subject: Subject?,
    onEntryClick: (TimetableEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { entry?.let { onEntryClick(it) } },
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (entry != null && subject != null) {
                Color(android.graphics.Color.parseColor(subject.color)).copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (entry != null && subject != null) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(android.graphics.Color.parseColor(subject.color)).copy(alpha = 0.3f)
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.width(80.dp)
            ) {
                Text(
                    text = timeSlot.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = timeSlot.endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            if (entry != null && subject != null) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = subject.code,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (entry.location != null) {
                        Text(
                            text = "📍 ${entry.location}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Surface(
                    color = Color(android.graphics.Color.parseColor(subject.color)),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = entry.sessionType.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                Text(
                    text = "Free Time",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
