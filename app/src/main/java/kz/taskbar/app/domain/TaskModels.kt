package kz.taskbar.app.domain

import java.util.Calendar

/** Identifiers are stored as stable ASCII values in Room. */
enum class TaskCategory(val title: String, val shortName: String) {
    WORK("Работа", "РАБОТА"),
    STUDY("Учёба", "УЧЁБА"),
    PERSONAL("Личное", "ЛИЧНОЕ"),
    HEALTH("Здоровье", "ЗДОРОВЬЕ");

    companion object {
        fun fromValue(raw: String): TaskCategory = entries.firstOrNull { it.name == raw } ?: PERSONAL
    }
}

enum class TaskPriority(val title: String) {
    LOW("Низкий"), MEDIUM("Средний"), HIGH("Высокий");
    companion object {
        fun fromValue(raw: String): TaskPriority = entries.firstOrNull { it.name == raw } ?: MEDIUM
    }
}

data class TaskDraft(
    val id: Long = 0L,
    val title: String,
    val description: String,
    val category: TaskCategory,
    val priority: TaskPriority,
    val dueAt: Long,
)

object TaskValidator {
    const val MAX_TITLE = 90
    const val MAX_DESCRIPTION = 1000

    /** Empty result means the draft is valid. */
    fun validate(title: String, description: String): String? = when {
        title.trim().isBlank() -> "Введите название задачи"
        title.trim().length > MAX_TITLE -> "Название не должно превышать $MAX_TITLE символов"
        description.length > MAX_DESCRIPTION -> "Описание слишком длинное"
        else -> null
    }
}

fun startOfDay(time: Long): Long = Calendar.getInstance().apply {
    timeInMillis = time
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

fun isSameDay(first: Long, second: Long) = startOfDay(first) == startOfDay(second)

fun shiftDays(time: Long, delta: Int): Long = Calendar.getInstance().apply {
    timeInMillis = time
    add(Calendar.DAY_OF_MONTH, delta)
}.timeInMillis

fun timeToday(hour: Int, minute: Int = 0): Long = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, hour)
    set(Calendar.MINUTE, minute)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis
