package kz.taskbar.app.domain

import kz.taskbar.app.data.TaskEntity

/** Deterministic business rules for dashboard, reports and unit coverage. */
data class TaskSummary(
    val total: Int,
    val completed: Int,
    val pending: Int,
    val overdue: Int,
    val progress: Int,
    val highPriorityPending: Int,
)

object TaskStatistics {
    fun summarize(tasks: List<TaskEntity>, now: Long): TaskSummary {
        val visible = tasks.filterNot { it.archived }
        val done = visible.count { it.completed }
        return TaskSummary(
            total = visible.size,
            completed = done,
            pending = visible.size - done,
            overdue = visible.count { !it.completed && it.dueAt < now },
            progress = ProgressCalculator.completionPercent(done, visible.size),
            highPriorityPending = visible.count { !it.completed && it.priority == TaskPriority.HIGH.name },
        )
    }

    fun tasksForCategory(tasks: List<TaskEntity>, category: TaskCategory): List<TaskEntity> =
        tasks.filter { !it.archived && it.category == category.name }

    fun search(tasks: List<TaskEntity>, query: String): List<TaskEntity> {
        val needle = query.trim()
        val active = tasks.filterNot { it.archived }
        if (needle.isEmpty()) return active
        return active.filter {
            it.title.contains(needle, ignoreCase = true) ||
                it.description.contains(needle, ignoreCase = true)
        }
    }
}
