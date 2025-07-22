package com.timetableoptimizer.ai.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.timetableoptimizer.ai.data.entities.TimetableEntry
import com.timetableoptimizer.ai.data.entities.SessionType
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

@Dao
interface TimetableEntryDao {
    
    @Query("SELECT * FROM timetable_entries WHERE timetableId = :timetableId ORDER BY timeSlotId")
    fun getEntriesByTimetableId(timetableId: String): Flow<List<TimetableEntry>>
    
    @Query("SELECT * FROM timetable_entries WHERE timetableId = :timetableId ORDER BY timeSlotId")
    suspend fun getEntriesByTimetableIdSync(timetableId: String): List<TimetableEntry>
    
    @Query("SELECT * FROM timetable_entries WHERE id = :id")
    suspend fun getEntryById(id: String): TimetableEntry?
    
    @Query("SELECT * FROM timetable_entries WHERE id = :id")
    fun getEntryByIdLiveData(id: String): LiveData<TimetableEntry?>
    
    @Query("SELECT * FROM timetable_entries WHERE subjectId = :subjectId")
    fun getEntriesBySubjectId(subjectId: String): Flow<List<TimetableEntry>>
    
    @Query("SELECT * FROM timetable_entries WHERE timeSlotId = :timeSlotId")
    suspend fun getEntriesByTimeSlotId(timeSlotId: String): List<TimetableEntry>
    
    @Query("SELECT * FROM timetable_entries WHERE timetableId = :timetableId AND sessionType = :sessionType")
    fun getEntriesBySessionType(timetableId: String, sessionType: SessionType): Flow<List<TimetableEntry>>
    
    @Query("SELECT * FROM timetable_entries WHERE timetableId = :timetableId AND isFixed = 1")
    suspend fun getFixedEntries(timetableId: String): List<TimetableEntry>
    
    @Query("""
        SELECT te.* FROM timetable_entries te 
        INNER JOIN time_slots ts ON te.timeSlotId = ts.id 
        WHERE te.timetableId = :timetableId AND ts.dayOfWeek = :dayOfWeek 
        ORDER BY ts.startTime
    """)
    fun getEntriesByDay(timetableId: String, dayOfWeek: DayOfWeek): Flow<List<TimetableEntry>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: TimetableEntry)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<TimetableEntry>)
    
    @Update
    suspend fun updateEntry(entry: TimetableEntry)
    
    @Delete
    suspend fun deleteEntry(entry: TimetableEntry)
    
    @Query("DELETE FROM timetable_entries WHERE id = :id")
    suspend fun deleteEntryById(id: String)
    
    @Query("DELETE FROM timetable_entries WHERE timetableId = :timetableId")
    suspend fun deleteEntriesByTimetableId(timetableId: String)
    
    @Query("DELETE FROM timetable_entries WHERE subjectId = :subjectId")
    suspend fun deleteEntriesBySubjectId(subjectId: String)
    
    @Query("SELECT COUNT(*) FROM timetable_entries WHERE timetableId = :timetableId")
    suspend fun getEntryCountByTimetableId(timetableId: String): Int
    
    @Query("SELECT SUM(duration) FROM timetable_entries WHERE timetableId = :timetableId")
    suspend fun getTotalDurationByTimetableId(timetableId: String): Int?
    
    @Query("""
        SELECT COUNT(*) FROM timetable_entries te1 
        INNER JOIN timetable_entries te2 ON te1.timeSlotId = te2.timeSlotId 
        WHERE te1.timetableId = :timetableId AND te2.timetableId = :timetableId 
        AND te1.id != te2.id
    """)
    suspend fun getConflictCount(timetableId: String): Int
}
