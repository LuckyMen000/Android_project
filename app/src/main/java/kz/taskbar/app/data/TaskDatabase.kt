package kz.taskbar.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Version 2 adds archived without deleting any existing user tasks. */
@Database(entities = [TaskEntity::class], version = 2, exportSchema = true)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun tasks(): TaskDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN archived INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
