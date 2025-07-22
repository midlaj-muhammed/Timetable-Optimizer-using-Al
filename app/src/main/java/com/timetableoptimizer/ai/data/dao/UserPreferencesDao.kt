package com.timetableoptimizer.ai.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.timetableoptimizer.ai.data.entities.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    
    @Query("SELECT * FROM user_preferences WHERE id = 'default'")
    fun getUserPreferences(): Flow<UserPreferences?>
    
    @Query("SELECT * FROM user_preferences WHERE id = 'default'")
    fun getUserPreferencesLiveData(): LiveData<UserPreferences?>
    
    @Query("SELECT * FROM user_preferences WHERE id = 'default'")
    suspend fun getUserPreferencesSync(): UserPreferences?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPreferences(preferences: UserPreferences)
    
    @Update
    suspend fun updateUserPreferences(preferences: UserPreferences)
    
    @Query("DELETE FROM user_preferences WHERE id = 'default'")
    suspend fun deleteUserPreferences()
    
    @Query("UPDATE user_preferences SET updatedAt = :timestamp WHERE id = 'default'")
    suspend fun updateTimestamp(timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_preferences SET theme = :theme, updatedAt = :timestamp WHERE id = 'default'")
    suspend fun updateTheme(theme: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_preferences SET language = :language, updatedAt = :timestamp WHERE id = 'default'")
    suspend fun updateLanguage(language: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_preferences SET notifications = :enabled, updatedAt = :timestamp WHERE id = 'default'")
    suspend fun updateNotifications(enabled: Boolean, timestamp: Long = System.currentTimeMillis())
}
