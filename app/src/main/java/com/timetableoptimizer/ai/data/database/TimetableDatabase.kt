package com.timetableoptimizer.ai.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.timetableoptimizer.ai.data.converters.Converters
import com.timetableoptimizer.ai.data.dao.*
import com.timetableoptimizer.ai.data.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.*

@Database(
    entities = [
        Subject::class,
        TimeSlot::class,
        Timetable::class,
        TimetableEntry::class,
        UserPreferences::class,
        Constraint::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TimetableDatabase : RoomDatabase() {
    
    abstract fun subjectDao(): SubjectDao
    abstract fun timeSlotDao(): TimeSlotDao
    abstract fun timetableDao(): TimetableDao
    abstract fun timetableEntryDao(): TimetableEntryDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun constraintDao(): ConstraintDao
    
    companion object {
        @Volatile
        private var INSTANCE: TimetableDatabase? = null
        
        fun getDatabase(context: Context): TimetableDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TimetableDatabase::class.java,
                    "timetable_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }
        }
        
        private suspend fun populateDatabase(database: TimetableDatabase) {
            // Insert default user preferences
            val defaultPreferences = UserPreferences()
            database.userPreferencesDao().insertUserPreferences(defaultPreferences)
            
            // Insert default time slots (Monday to Friday, 8 AM to 6 PM)
            val timeSlots = mutableListOf<TimeSlot>()
            val days = listOf(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
            )
            
            for (day in days) {
                for (hour in 8..17) {
                    val startTime = LocalTime.of(hour, 0)
                    val endTime = LocalTime.of(hour + 1, 0)
                    val timeSlot = TimeSlot(
                        id = UUID.randomUUID().toString(),
                        dayOfWeek = day,
                        startTime = startTime,
                        endTime = endTime,
                        isAvailable = true,
                        isPreferred = hour in 9..16 // Prefer 9 AM to 4 PM
                    )
                    timeSlots.add(timeSlot)
                }
            }
            database.timeSlotDao().insertTimeSlots(timeSlots)
            
            // Insert default constraints
            val constraints = listOf(
                Constraint(
                    id = UUID.randomUUID().toString(),
                    name = "No Time Conflicts",
                    description = "Prevent scheduling multiple subjects at the same time",
                    type = ConstraintType.TIME_CONFLICT,
                    severity = ConstraintSeverity.HARD,
                    parameters = "{}",
                    weight = 1000.0,
                    violationPenalty = 1000.0
                ),
                Constraint(
                    id = UUID.randomUUID().toString(),
                    name = "Maximum Hours Per Day",
                    description = "Limit the maximum study hours per day",
                    type = ConstraintType.MAX_HOURS_PER_DAY,
                    severity = ConstraintSeverity.SOFT,
                    parameters = "{\"maxHours\": 8}",
                    weight = 100.0,
                    violationPenalty = 50.0
                ),
                Constraint(
                    id = UUID.randomUUID().toString(),
                    name = "Minimum Break Duration",
                    description = "Ensure minimum break time between sessions",
                    type = ConstraintType.MIN_BREAK_DURATION,
                    severity = ConstraintSeverity.SOFT,
                    parameters = "{\"minBreakMinutes\": 15}",
                    weight = 80.0,
                    violationPenalty = 30.0
                )
            )
            database.constraintDao().insertConstraints(constraints)
        }
    }
}
