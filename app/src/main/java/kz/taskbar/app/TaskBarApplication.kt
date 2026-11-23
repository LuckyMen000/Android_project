package kz.taskbar.app

import android.app.Application
import android.os.StrictMode
import kz.taskbar.app.diagnostics.TaskBarLog
import androidx.room.Room
import kz.taskbar.app.data.LocalTaskRepository
import kz.taskbar.app.data.NetworkQuoteRepository
import kz.taskbar.app.data.PreferencesRepository
import kz.taskbar.app.data.TaskDatabase

class TaskBarApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().detectActivityLeaks()
                    .penaltyLog().build()
            )
            TaskBarLog.i(TaskBarLog.APP, "StrictMode enabled in debug")
        }
    }

    val database: TaskDatabase by lazy {
        Room.databaseBuilder(applicationContext, TaskDatabase::class.java, "taskbar.db")
            .addMigrations(TaskDatabase.MIGRATION_1_2)
            .build()
    }
    val preferences: PreferencesRepository by lazy { PreferencesRepository(applicationContext) }
    val repository: LocalTaskRepository by lazy { LocalTaskRepository(database.tasks(), preferences) }
    val quoteRepository: NetworkQuoteRepository by lazy { NetworkQuoteRepository() }
}
