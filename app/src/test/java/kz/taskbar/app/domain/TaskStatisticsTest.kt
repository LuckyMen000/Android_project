package kz.taskbar.app.domain

import kz.taskbar.app.data.TaskEntity
import org.junit.Assert.*
import org.junit.Test

class TaskStatisticsTest {
    private fun task(id: Long, completed: Boolean = false, due: Long = 500,
                     category: String = "WORK", priority: String = "MEDIUM",
                     archived: Boolean = false): TaskEntity = TaskEntity(
        id = id, title = "Task $id", description = "notes", category = category,
        priority = priority, dueAt = due, completed = completed, archived = archived,
    )

    @Test fun emptyListGivesZeroSummary() {
        assertEquals(TaskSummary(0, 0, 0, 0, 0, 0), TaskStatistics.summarize(emptyList(), 1000))
    }

    @Test fun summaryCountsOverdueAndHighPriority() {
        val tasks = listOf(task(1, due = 5, priority = "HIGH"),
            task(2, completed = true, due = 5), task(3, due = 1200))
        assertEquals(TaskSummary(3, 1, 2, 1, 33, 1), TaskStatistics.summarize(tasks, 1000))
    }

    @Test fun completedPastDeadlineIsNotOverdue() {
        assertEquals(0, TaskStatistics.summarize(listOf(task(1, completed = true, due = 1)), 100).overdue)
    }

    @Test fun archivedTasksAreExcludedFromMetrics() {
        val tasks = listOf(task(1, archived = true), task(2))
        assertEquals(1, TaskStatistics.summarize(tasks, 1000).total)
    }

    @Test fun categoryFiltersAndExcludesArchived() {
        val tasks = listOf(task(1), task(2, category = "HEALTH"), task(3, archived = true))
        assertEquals(listOf(1L), TaskStatistics.tasksForCategory(tasks, TaskCategory.WORK).map { it.id })
    }

    @Test fun caseInsensitiveSearchFindsTitle() {
        val tasks = listOf(task(1).copy(title = "Сдать ОТЧЁТ"), task(2))
        assertEquals(listOf(1L), TaskStatistics.search(tasks, "отчёт").map { it.id })
    }

    @Test fun searchFindsDescriptionAndTrimsQuery() {
        val tasks = listOf(task(1).copy(description = "клиентский звонок"), task(2))
        assertEquals(listOf(1L), TaskStatistics.search(tasks, " ЗВОНОК ").map { it.id })
    }

    @Test fun emptyQueryReturnsNonArchived() {
        assertEquals(1, TaskStatistics.search(listOf(task(1), task(2, archived = true)), "  ").size)
    }
}
