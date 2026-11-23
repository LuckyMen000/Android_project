package kz.taskbar.app.data

import kz.taskbar.app.diagnostics.TaskBarLog
import kz.taskbar.app.domain.TaskDraft
import kz.taskbar.app.domain.TaskValidator
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    val tasks: Flow<List<TaskEntity>>
    suspend fun save(draft: TaskDraft)
    suspend fun setCompleted(task: TaskEntity, completed: Boolean)
    suspend fun delete(task: TaskEntity)
    suspend fun clearCompleted()
    suspend fun populateIfFirstLaunch()
}

class LocalTaskRepository(
    private val dao: TaskDao,
    private val preferences: PreferencesRepository,
) : TaskRepository {
    override val tasks: Flow<List<TaskEntity>> = dao.observeAll()

    override suspend fun save(draft: TaskDraft) {
        val error = TaskValidator.validate(draft.title, draft.description)
        require(error == null) { error ?: "Неверная задача" }
        val newTask = TaskEntity(
            id = draft.id,
            title = draft.title.trim(),
            description = draft.description.trim(),
            category = draft.category.name,
            priority = draft.priority.name,
            dueAt = draft.dueAt,
        )
        // Editing must preserve completion state and creation timestamp.
        if (draft.id == 0L) {
            dao.insert(newTask)
            TaskBarLog.i(TaskBarLog.TASK, "Task created")
        } else {
            val original = dao.findById(draft.id) ?: return
            dao.update(newTask.copy(createdAt = original.createdAt,
                completed = original.completed, completedAt = original.completedAt,
                archived = original.archived))
            TaskBarLog.i(TaskBarLog.TASK, "Task updated id=${draft.id}")
        }
    }

    override suspend fun setCompleted(task: TaskEntity, completed: Boolean) {
        dao.update(task.copy(completed = completed,
            completedAt = if (completed) System.currentTimeMillis() else null))
        TaskBarLog.d(TaskBarLog.TASK, "Task completion updated id=${task.id} completed=$completed")
    }

    override suspend fun delete(task: TaskEntity) {
        dao.delete(task)
        TaskBarLog.w(TaskBarLog.TASK, "Task deleted id=${task.id}")
    }
    override suspend fun clearCompleted() {
        dao.deleteCompleted()
        TaskBarLog.i(TaskBarLog.DATABASE, "Completed tasks removed")
    }

    override suspend fun populateIfFirstLaunch() {
        if (preferences.hasSeeded()) return
        if (dao.totalCount() == 0) dao.insertAll(DemoTasks.create())
        preferences.markSeeded()
    }
}
