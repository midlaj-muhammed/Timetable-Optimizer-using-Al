package com.timetableoptimizer.ai.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.timetableoptimizer.ai.data.entities.Timetable
import com.timetableoptimizer.ai.data.entities.TimetableStatus
import com.timetableoptimizer.ai.data.entities.TimetableType
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {
    
    @Query("SELECT * FROM timetables ORDER BY updatedAt DESC")
    fun getAllTimetables(): Flow<List<Timetable>>
    
    @Query("SELECT * FROM timetables WHERE status != 'ARCHIVED' ORDER BY updatedAt DESC")
    fun getActiveTimetables(): Flow<List<Timetable>>
    
    @Query("SELECT * FROM timetables WHERE id = :id")
    suspend fun getTimetableById(id: String): Timetable?
    
    @Query("SELECT * FROM timetables WHERE id = :id")
    fun getTimetableByIdLiveData(id: String): LiveData<Timetable?>
    
    @Query("SELECT * FROM timetables WHERE type = :type ORDER BY updatedAt DESC")
    fun getTimetablesByType(type: TimetableType): Flow<List<Timetable>>
    
    @Query("SELECT * FROM timetables WHERE status = :status ORDER BY updatedAt DESC")
    fun getTimetablesByStatus(status: TimetableStatus): Flow<List<Timetable>>
    
    @Query("SELECT * FROM timetables WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteTimetables(): Flow<List<Timetable>>
    
    @Query("SELECT * FROM timetables WHERE isTemplate = 1 ORDER BY name ASC")
    fun getTemplateTimetables(): Flow<List<Timetable>>
    
    @Query("SELECT * FROM timetables WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTimetables(query: String): Flow<List<Timetable>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetable(timetable: Timetable)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetables(timetables: List<Timetable>)
    
    @Update
    suspend fun updateTimetable(timetable: Timetable)
    
    @Delete
    suspend fun deleteTimetable(timetable: Timetable)
    
    @Query("DELETE FROM timetables WHERE id = :id")
    suspend fun deleteTimetableById(id: String)
    
    @Query("UPDATE timetables SET status = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateTimetableStatus(id: String, status: TimetableStatus, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE timetables SET isFavorite = :isFavorite, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateTimetableFavorite(id: String, isFavorite: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE timetables SET optimizationScore = :score, lastOptimizedAt = :timestamp, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateOptimizationScore(id: String, score: Double, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT COUNT(*) FROM timetables WHERE status != 'ARCHIVED'")
    suspend fun getActiveTimetableCount(): Int
    
    @Query("SELECT * FROM timetables WHERE status = 'ACTIVE' LIMIT 1")
    suspend fun getCurrentActiveTimetable(): Timetable?
}
