package com.timetableoptimizer.ai.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.timetableoptimizer.ai.data.entities.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    
    @Query("SELECT * FROM subjects WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveSubjects(): Flow<List<Subject>>
    
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<Subject>>
    
    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: String): Subject?
    
    @Query("SELECT * FROM subjects WHERE id = :id")
    fun getSubjectByIdLiveData(id: String): LiveData<Subject?>
    
    @Query("SELECT * FROM subjects WHERE code = :code")
    suspend fun getSubjectByCode(code: String): Subject?
    
    @Query("SELECT * FROM subjects WHERE priority = :priority AND isActive = 1")
    fun getSubjectsByPriority(priority: String): Flow<List<Subject>>
    
    @Query("SELECT * FROM subjects WHERE difficulty = :difficulty AND isActive = 1")
    fun getSubjectsByDifficulty(difficulty: String): Flow<List<Subject>>
    
    @Query("SELECT * FROM subjects WHERE name LIKE '%' || :query || '%' OR code LIKE '%' || :query || '%'")
    fun searchSubjects(query: String): Flow<List<Subject>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>)
    
    @Update
    suspend fun updateSubject(subject: Subject)
    
    @Delete
    suspend fun deleteSubject(subject: Subject)
    
    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubjectById(id: String)
    
    @Query("UPDATE subjects SET isActive = 0 WHERE id = :id")
    suspend fun deactivateSubject(id: String)
    
    @Query("UPDATE subjects SET isActive = 1 WHERE id = :id")
    suspend fun activateSubject(id: String)
    
    @Query("SELECT COUNT(*) FROM subjects WHERE isActive = 1")
    suspend fun getActiveSubjectCount(): Int
    
    @Query("SELECT SUM(estimatedHoursPerWeek) FROM subjects WHERE isActive = 1")
    suspend fun getTotalEstimatedHours(): Int?
}
