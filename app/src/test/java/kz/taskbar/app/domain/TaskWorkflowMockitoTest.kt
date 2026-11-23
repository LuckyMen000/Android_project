package kz.taskbar.app.domain

import kz.taskbar.app.data.TaskEntity
import kz.taskbar.app.data.TaskRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TaskWorkflowMockitoTest {
    private val repository = mock<TaskRepository>()
    private val workflow = TaskWorkflow(repository)
    private val draft = TaskDraft(title = "Новый отчёт", description = "Короткая заметка",
        category = TaskCategory.WORK, priority = TaskPriority.HIGH, dueAt = 123L)
    private val task = TaskEntity(id = 42, title = "Новый отчёт", dueAt = 123L)

    @Test fun addValidTaskDelegatesToRepository() = runTest {
        workflow.add(draft)
        verify(repository).save(draft)
    }

    @Test fun rejectInvalidBeforeRepository() = runTest {
        try {
            workflow.add(draft.copy(title = "  "))
            fail("Expected validation failure")
        } catch (_: IllegalArgumentException) { }
        org.mockito.kotlin.verifyNoInteractions(repository)
    }

    @Test fun completionIsVerified() = runTest {
        workflow.complete(task)
        verify(repository).setCompleted(task, true)
    }

    @Test fun reopenIsVerified() = runTest {
        workflow.reopen(task)
        verify(repository).setCompleted(task, false)
    }

    @Test fun removalIsVerified() = runTest {
        workflow.remove(task)
        verify(repository).delete(task)
    }

    @Test fun repositoryFlowIsStubbed() = runTest {
        whenever(repository.tasks).thenReturn(flowOf(listOf(task)))
        assertEquals(42L, workflow.tasks.first().single().id)
    }
}
