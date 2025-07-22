package com.timetableoptimizer.ai.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.timetableoptimizer.ai.data.database.TimetableDatabase
import com.timetableoptimizer.ai.data.entities.Subject
import com.timetableoptimizer.ai.data.entities.Priority
import com.timetableoptimizer.ai.data.entities.Difficulty
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class SubjectDaoTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: TimetableDatabase
    private lateinit var subjectDao: SubjectDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TimetableDatabase::class.java
        ).allowMainThreadQueries().build()
        
        subjectDao = database.subjectDao()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun insertAndGetSubject() = runBlocking {
        val subject = Subject(
            id = "test_subject",
            name = "Test Subject",
            code = "TEST101",
            color = "#FF0000",
            credits = 3,
            priority = Priority.HIGH,
            estimatedHoursPerWeek = 4,
            difficulty = Difficulty.MEDIUM
        )
        
        subjectDao.insertSubject(subject)
        
        val retrievedSubject = subjectDao.getSubjectById("test_subject")
        
        assertNotNull(retrievedSubject)
        assertEquals(subject.name, retrievedSubject?.name)
        assertEquals(subject.code, retrievedSubject?.code)
        assertEquals(subject.priority, retrievedSubject?.priority)
    }
    
    @Test
    fun getAllActiveSubjects() = runBlocking {
        val subjects = listOf(
            Subject(
                id = "subject1",
                name = "Subject 1",
                code = "SUB1",
                color = "#FF0000",
                credits = 3,
                priority = Priority.HIGH,
                estimatedHoursPerWeek = 4,
                difficulty = Difficulty.MEDIUM,
                isActive = true
            ),
            Subject(
                id = "subject2",
                name = "Subject 2",
                code = "SUB2",
                color = "#00FF00",
                credits = 2,
                priority = Priority.LOW,
                estimatedHoursPerWeek = 2,
                difficulty = Difficulty.EASY,
                isActive = false
            )
        )
        
        subjects.forEach { subjectDao.insertSubject(it) }
        
        val activeSubjects = subjectDao.getAllActiveSubjects().first()
        
        assertEquals(1, activeSubjects.size)
        assertEquals("subject1", activeSubjects[0].id)
        assertTrue(activeSubjects[0].isActive)
    }
    
    @Test
    fun updateSubject() = runBlocking {
        val subject = Subject(
            id = "test_subject",
            name = "Original Name",
            code = "TEST101",
            color = "#FF0000",
            credits = 3,
            priority = Priority.LOW,
            estimatedHoursPerWeek = 2,
            difficulty = Difficulty.EASY
        )
        
        subjectDao.insertSubject(subject)
        
        val updatedSubject = subject.copy(
            name = "Updated Name",
            priority = Priority.HIGH,
            updatedAt = System.currentTimeMillis()
        )
        
        subjectDao.updateSubject(updatedSubject)
        
        val retrievedSubject = subjectDao.getSubjectById("test_subject")
        
        assertNotNull(retrievedSubject)
        assertEquals("Updated Name", retrievedSubject?.name)
        assertEquals(Priority.HIGH, retrievedSubject?.priority)
    }
    
    @Test
    fun deleteSubject() = runBlocking {
        val subject = Subject(
            id = "test_subject",
            name = "Test Subject",
            code = "TEST101",
            color = "#FF0000",
            credits = 3,
            priority = Priority.MEDIUM,
            estimatedHoursPerWeek = 3,
            difficulty = Difficulty.MEDIUM
        )
        
        subjectDao.insertSubject(subject)
        
        var retrievedSubject = subjectDao.getSubjectById("test_subject")
        assertNotNull(retrievedSubject)
        
        subjectDao.deleteSubject(subject)
        
        retrievedSubject = subjectDao.getSubjectById("test_subject")
        assertNull(retrievedSubject)
    }
    
    @Test
    fun searchSubjects() = runBlocking {
        val subjects = listOf(
            Subject(
                id = "math_subject",
                name = "Mathematics",
                code = "MATH101",
                color = "#FF0000",
                credits = 3,
                priority = Priority.HIGH,
                estimatedHoursPerWeek = 4,
                difficulty = Difficulty.HARD
            ),
            Subject(
                id = "physics_subject",
                name = "Physics",
                code = "PHYS101",
                color = "#00FF00",
                credits = 3,
                priority = Priority.MEDIUM,
                estimatedHoursPerWeek = 3,
                difficulty = Difficulty.MEDIUM
            )
        )
        
        subjects.forEach { subjectDao.insertSubject(it) }
        
        val mathResults = subjectDao.searchSubjects("Math").first()
        assertEquals(1, mathResults.size)
        assertEquals("Mathematics", mathResults[0].name)
        
        val codeResults = subjectDao.searchSubjects("101").first()
        assertEquals(2, codeResults.size)
    }
    
    @Test
    fun getSubjectsByPriority() = runBlocking {
        val subjects = listOf(
            Subject(
                id = "high_priority",
                name = "High Priority Subject",
                code = "HIGH101",
                color = "#FF0000",
                credits = 3,
                priority = Priority.HIGH,
                estimatedHoursPerWeek = 4,
                difficulty = Difficulty.HARD
            ),
            Subject(
                id = "low_priority",
                name = "Low Priority Subject",
                code = "LOW101",
                color = "#00FF00",
                credits = 2,
                priority = Priority.LOW,
                estimatedHoursPerWeek = 2,
                difficulty = Difficulty.EASY
            )
        )
        
        subjects.forEach { subjectDao.insertSubject(it) }
        
        val highPrioritySubjects = subjectDao.getSubjectsByPriority(Priority.HIGH.name).first()
        assertEquals(1, highPrioritySubjects.size)
        assertEquals(Priority.HIGH, highPrioritySubjects[0].priority)
    }
    
    @Test
    fun getActiveSubjectCount() = runBlocking {
        val subjects = listOf(
            Subject(
                id = "active1",
                name = "Active Subject 1",
                code = "ACT1",
                color = "#FF0000",
                credits = 3,
                priority = Priority.HIGH,
                estimatedHoursPerWeek = 4,
                difficulty = Difficulty.MEDIUM,
                isActive = true
            ),
            Subject(
                id = "active2",
                name = "Active Subject 2",
                code = "ACT2",
                color = "#00FF00",
                credits = 2,
                priority = Priority.LOW,
                estimatedHoursPerWeek = 2,
                difficulty = Difficulty.EASY,
                isActive = true
            ),
            Subject(
                id = "inactive1",
                name = "Inactive Subject",
                code = "INACT1",
                color = "#0000FF",
                credits = 1,
                priority = Priority.MEDIUM,
                estimatedHoursPerWeek = 1,
                difficulty = Difficulty.EASY,
                isActive = false
            )
        )
        
        subjects.forEach { subjectDao.insertSubject(it) }
        
        val count = subjectDao.getActiveSubjectCount()
        assertEquals(2, count)
    }
}
