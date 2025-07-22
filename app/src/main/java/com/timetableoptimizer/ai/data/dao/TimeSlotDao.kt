package com.timetableoptimizer.ai.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.timetableoptimizer.ai.data.entities.TimeSlot
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalTime

@Dao
interface TimeSlotDao {
    
    @Query("SELECT * FROM time_slots WHERE isAvailable = 1 ORDER BY dayOfWeek, startTime")
    fun getAllAvailableTimeSlots(): Flow<List<TimeSlot>>
    
    @Query("SELECT * FROM time_slots ORDER BY dayOfWeek, startTime")
    fun getAllTimeSlots(): Flow<List<TimeSlot>>
    
    @Query("SELECT * FROM time_slots WHERE id = :id")
    suspend fun getTimeSlotById(id: String): TimeSlot?
    
    @Query("SELECT * FROM time_slots WHERE id = :id")
    fun getTimeSlotByIdLiveData(id: String): LiveData<TimeSlot?>
    
    @Query("SELECT * FROM time_slots WHERE dayOfWeek = :dayOfWeek ORDER BY startTime")
    fun getTimeSlotsByDay(dayOfWeek: DayOfWeek): Flow<List<TimeSlot>>
    
    @Query("SELECT * FROM time_slots WHERE dayOfWeek = :dayOfWeek AND isAvailable = 1 ORDER BY startTime")
    fun getAvailableTimeSlotsByDay(dayOfWeek: DayOfWeek): Flow<List<TimeSlot>>
    
    @Query("SELECT * FROM time_slots WHERE isPreferred = 1 AND isAvailable = 1 ORDER BY dayOfWeek, startTime")
    fun getPreferredTimeSlots(): Flow<List<TimeSlot>>
    
    @Query("SELECT * FROM time_slots WHERE startTime >= :startTime AND endTime <= :endTime AND isAvailable = 1")
    fun getTimeSlotsByTimeRange(startTime: LocalTime, endTime: LocalTime): Flow<List<TimeSlot>>
    
    @Query("SELECT * FROM time_slots WHERE room = :room AND isAvailable = 1 ORDER BY dayOfWeek, startTime")
    fun getTimeSlotsByRoom(room: String): Flow<List<TimeSlot>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeSlot(timeSlot: TimeSlot)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeSlots(timeSlots: List<TimeSlot>)
    
    @Update
    suspend fun updateTimeSlot(timeSlot: TimeSlot)
    
    @Delete
    suspend fun deleteTimeSlot(timeSlot: TimeSlot)
    
    @Query("DELETE FROM time_slots WHERE id = :id")
    suspend fun deleteTimeSlotById(id: String)
    
    @Query("UPDATE time_slots SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun updateTimeSlotAvailability(id: String, isAvailable: Boolean)
    
    @Query("UPDATE time_slots SET isPreferred = :isPreferred WHERE id = :id")
    suspend fun updateTimeSlotPreference(id: String, isPreferred: Boolean)
    
    @Query("SELECT COUNT(*) FROM time_slots WHERE isAvailable = 1")
    suspend fun getAvailableTimeSlotCount(): Int
    
    @Query("SELECT COUNT(*) FROM time_slots WHERE dayOfWeek = :dayOfWeek AND isAvailable = 1")
    suspend fun getAvailableTimeSlotCountByDay(dayOfWeek: DayOfWeek): Int
    
    @Query("""
        SELECT * FROM time_slots 
        WHERE dayOfWeek = :dayOfWeek 
        AND startTime < :endTime 
        AND endTime > :startTime 
        AND isAvailable = 1
    """)
    suspend fun getOverlappingTimeSlots(dayOfWeek: DayOfWeek, startTime: LocalTime, endTime: LocalTime): List<TimeSlot>
}
