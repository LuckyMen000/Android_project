package kz.taskbar.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Real SQLite/Room behavior on a connected emulator/device (in-memory DB). */
@RunWith(AndroidJUnit4::class)
class TaskDaoInstrumentedTest {
    private lateinit var database: TaskDatabase
    private lateinit var dao: TaskDao

    @Before fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
            .allowMainThreadQueries().build()
        dao = database.tasks()
    }
    @After fun tearDown() { database.close() }

    @Test fun insertUpdateAndDelete() = runBlocking {
        val id = dao.insert(TaskEntity(title = "Проверка DAO", dueAt = 100))
        val saved = dao.findById(id) ?: error("Not inserted")
        assertEquals("Проверка DAO", saved.title)
        dao.update(saved.copy(completed = true, completedAt = 1000L))
        assertTrue(dao.findById(id)!!.completed)
        dao.delete(dao.findById(id)!!)
        assertNull(dao.findById(id))
    }

    @Test fun flowTracksRowsAndClearCompleted() = runBlocking {
        dao.insert(TaskEntity(title = "Active", dueAt = 10))
        dao.insert(TaskEntity(title = "Done", dueAt = 20, completed = true))
        assertEquals(2, dao.observeAll().first().size)
        dao.deleteCompleted()
        assertEquals(1, dao.totalCount())
        assertEquals("Active", dao.observeAll().first().single().title)
    }
}
