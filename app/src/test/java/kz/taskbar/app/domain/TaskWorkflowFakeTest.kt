package kz.taskbar.app.domain

import kz.taskbar.app.data.TaskEntity
import kz.taskbar.app.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/** A fake owns in-memory state; unlike a mock, it implements realistic behavior. */
private class FakeTasks : TaskRepository {
    override val tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    override suspend fun save(draft: TaskDraft) {
        tasks.value += TaskEntity(id = (tasks.value.size + 1).toLong(),
            title = draft.title, description = draft.description, dueAt = draft.dueAt,
            category = draft.category.name, priority = draft.priority.name)
    }
    override suspend fun setCompleted(task: TaskEntity, completed: Boolean) {
        tasks.value = tasks.value.map { if (it.id == task.id) it.copy(completed = completed) else it }
    }
    override suspend fun delete(task: TaskEntity) { tasks.value = tasks.value.filterNot { it.id == task.id } }
    override suspend fun clearCompleted() { tasks.value = tasks.value.filterNot { it.completed } }
    override suspend fun populateIfFirstLaunch() = Unit
}

class TaskWorkflowFakeTest {
    @Test fun fakeRepositoryEmitsChangesWithTestDispatcher() = runTest {
        val fake = FakeTasks()
        val workflow = TaskWorkflow(fake)
        val dispatcher = StandardTestDispatcher(testScheduler)
        val job = backgroundScope.launch(dispatcher) {
            workflow.add(TaskDraft(title = "Первая", description = "", category = TaskCategory.STUDY,
                priority = TaskPriority.LOW, dueAt = 100L))
        }
        testScheduler.advanceUntilIdle()
        job.join()
        assertEquals("Первая", workflow.tasks.first().single().title)
        workflow.complete(fake.tasks.value.single())
        assertTrue(workflow.tasks.first().single().completed)
    }
}
