package kz.taskbar.app.data

import kz.taskbar.app.domain.shiftDays
import kz.taskbar.app.domain.timeToday

/** Clearly removable demo data on the very first launch. */
object DemoTasks {
    fun create(): List<TaskEntity> {
        val today = timeToday(9)
        return listOf(
            TaskEntity(title = "Подготовить презентацию проекта", description = "Собрать ключевые результаты и следующие шаги", category = "WORK", priority = "HIGH", dueAt = timeToday(11, 30)),
            TaskEntity(title = "Разобрать материалы по Android", description = "Повторить основы отладки и тестирования", category = "STUDY", priority = "MEDIUM", dueAt = timeToday(15)),
            TaskEntity(title = "Прогуляться 30 минут", description = "Небольшая перезагрузка после рабочего дня", category = "HEALTH", priority = "LOW", dueAt = timeToday(19)),
            TaskEntity(title = "Проверить почту и задачи", description = "Входящие, ответить на важные сообщения", category = "WORK", priority = "MEDIUM", dueAt = timeToday(9), completed = true, completedAt = timeToday(9, 30)),
            TaskEntity(title = "Составить план на завтра", description = "Определить три главных приоритета", category = "PERSONAL", priority = "MEDIUM", dueAt = shiftDays(today, 1)),
            TaskEntity(title = "Разобрать список целей", description = "Подвести промежуточные итоги", category = "PERSONAL", priority = "LOW", dueAt = shiftDays(today, -1), completed = true, completedAt = shiftDays(today, -1)),
        )
    }
}
