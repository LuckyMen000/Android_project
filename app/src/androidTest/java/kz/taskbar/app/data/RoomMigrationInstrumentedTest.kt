package kz.taskbar.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Real v1 -> v2 schema upgrade, created exactly as the original v1 entity was stored.
 * Uses a legacy SQLite file, so testing does not rely on invented Room identity hashes.
 */
@RunWith(AndroidJUnit4::class)
class RoomMigrationInstrumentedTest {
    @Test fun migrationKeepsExistingTaskAndAddsArchivedFlag() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "migration-v1-v2-${System.nanoTime()}.db"
        val file = context.getDatabasePath(name)
        file.parentFile?.mkdirs()
        try {
            SQLiteDatabase.openOrCreateDatabase(file, null).use { legacy ->
                legacy.execSQL("""CREATE TABLE IF NOT EXISTS `tasks` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `title` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `category` TEXT NOT NULL,
                    `priority` TEXT NOT NULL,
                    `dueAt` INTEGER NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `completed` INTEGER NOT NULL,
                    `completedAt` INTEGER
                )""")
                legacy.execSQL("""INSERT INTO tasks
                    (id, title, description, category, priority, dueAt, createdAt, completed, completedAt)
                    VALUES (7, 'Legacy Task', '', 'WORK', 'HIGH', 100, 5, 0, NULL)""")
                legacy.version = 1
            }
            val migrated = Room.databaseBuilder(context, TaskDatabase::class.java, name)
                .addMigrations(TaskDatabase.MIGRATION_1_2)
                .build()
            try {
                val task = migrated.tasks().findById(7)
                assertNotNull(task)
                assertEquals("Legacy Task", task!!.title)
                assertFalse(task.archived)
                assertEquals("HIGH", task.priority)
            } finally { migrated.close() }
        } finally { context.deleteDatabase(name) }
    }
}
