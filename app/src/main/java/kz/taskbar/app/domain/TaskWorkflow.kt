package kz.taskbar.app.domain

import kz.taskbar.app.data.TaskEntity
import kz.taskbar.app.data.TaskRepository
import kotlinx.coroutines.flow.Flow

/** UseCase: no Android dependency, easily isolated via mocks or fakes. */
class TaskWorkflow(private val repository: TaskRepository) {
    val tasks: Flow<List<TaskEntity>> get() = repository.tasks

    suspend fun add(draft: TaskDraft) {
        require(TaskValidator.validate(draft.title, draft.description) == null) {
            "Некорректные данные задачи"
        }
        repository.save(draft)
    }

    suspend fun complete(task: TaskEntity) = repository.setCompleted(task, true)
    suspend fun reopen(task: TaskEntity) = repository.setCompleted(task, false)
    suspend fun remove(task: TaskEntity) = repository.delete(task)
}
