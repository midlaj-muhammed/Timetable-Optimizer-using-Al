package com.timetableoptimizer.ai

import android.app.Application
import com.timetableoptimizer.ai.data.database.TimetableDatabase

class TimetableOptimizerApplication : Application() {
    
    val database by lazy { TimetableDatabase.getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: TimetableOptimizerApplication
            private set
    }
}
