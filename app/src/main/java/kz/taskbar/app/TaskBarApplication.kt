package kz.taskbar.app

import android.app.Application
import androidx.room.Room
import kz.taskbar.app.data.LocalTaskRepository
import kz.taskbar.app.data.NetworkQuoteRepository
import kz.taskbar.app.data.PreferencesRepository
import kz.taskbar.app.data.TaskDatabase

class TaskBarApplication : Application() {
    val database: TaskDatabase by lazy {
        Room.databaseBuilder(applicationContext, TaskDatabase::class.java, "taskbar.db").build()
    }
    val preferences: PreferencesRepository by lazy { PreferencesRepository(applicationContext) }
    val repository: LocalTaskRepository by lazy { LocalTaskRepository(database.tasks(), preferences) }
    val quoteRepository: NetworkQuoteRepository by lazy { NetworkQuoteRepository() }
}
