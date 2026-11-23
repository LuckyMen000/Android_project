package kz.taskbar.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kz.taskbar.app.domain.TaskDraft

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String = "",
    val category: String = "PERSONAL",
    val priority: String = "MEDIUM",
    val dueAt: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val completed: Boolean = false,
    val completedAt: Long? = null,
    @androidx.room.ColumnInfo(defaultValue = "0") val archived: Boolean = false,
)

fun TaskEntity.asDraft() = TaskDraft(
    id = id, title = title, description = description,
    category = kz.taskbar.app.domain.TaskCategory.fromValue(category),
    priority = kz.taskbar.app.domain.TaskPriority.fromValue(priority), dueAt = dueAt,
)
