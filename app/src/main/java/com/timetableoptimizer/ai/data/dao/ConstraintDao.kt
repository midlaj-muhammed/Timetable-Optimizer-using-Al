package com.timetableoptimizer.ai.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.timetableoptimizer.ai.data.entities.Constraint
import com.timetableoptimizer.ai.data.entities.ConstraintType
import com.timetableoptimizer.ai.data.entities.ConstraintSeverity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConstraintDao {
    
    @Query("SELECT * FROM constraints WHERE isActive = 1 ORDER BY severity DESC, weight DESC")
    fun getAllActiveConstraints(): Flow<List<Constraint>>
    
    @Query("SELECT * FROM constraints ORDER BY severity DESC, weight DESC")
    fun getAllConstraints(): Flow<List<Constraint>>
    
    @Query("SELECT * FROM constraints WHERE id = :id")
    suspend fun getConstraintById(id: String): Constraint?
    
    @Query("SELECT * FROM constraints WHERE id = :id")
    fun getConstraintByIdLiveData(id: String): LiveData<Constraint?>
    
    @Query("SELECT * FROM constraints WHERE type = :type AND isActive = 1")
    fun getConstraintsByType(type: ConstraintType): Flow<List<Constraint>>
    
    @Query("SELECT * FROM constraints WHERE severity = :severity AND isActive = 1")
    fun getConstraintsBySeverity(severity: ConstraintSeverity): Flow<List<Constraint>>
    
    @Query("SELECT * FROM constraints WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchConstraints(query: String): Flow<List<Constraint>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConstraint(constraint: Constraint)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConstraints(constraints: List<Constraint>)
    
    @Update
    suspend fun updateConstraint(constraint: Constraint)
    
    @Delete
    suspend fun deleteConstraint(constraint: Constraint)
    
    @Query("DELETE FROM constraints WHERE id = :id")
    suspend fun deleteConstraintById(id: String)
    
    @Query("UPDATE constraints SET isActive = :isActive, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateConstraintStatus(id: String, isActive: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE constraints SET weight = :weight, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateConstraintWeight(id: String, weight: Double, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT COUNT(*) FROM constraints WHERE isActive = 1")
    suspend fun getActiveConstraintCount(): Int
    
    @Query("SELECT COUNT(*) FROM constraints WHERE type = :type AND isActive = 1")
    suspend fun getActiveConstraintCountByType(type: ConstraintType): Int
}
