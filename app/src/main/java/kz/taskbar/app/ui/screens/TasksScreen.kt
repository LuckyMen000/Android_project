package kz.taskbar.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.taskbar.app.data.TaskEntity
import kz.taskbar.app.domain.TaskCategory
import kz.taskbar.app.ui.components.*
import kz.taskbar.app.ui.theme.TB

private enum class StatusFilter(val text: String) {
    ALL("Все"), ACTIVE("В процессе"), DONE("Готово")
}

@Composable
fun TasksScreen(tasks: List<TaskEntity>, onNew: () -> Unit, onToggle: (TaskEntity) -> Unit,
                onEdit: (TaskEntity) -> Unit, onDelete: (TaskEntity) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var search by rememberSaveable { mutableStateOf("") }
    var status by remember { mutableStateOf(StatusFilter.ALL) }
    var category by remember { mutableStateOf<TaskCategory?>(null) }
    var prioritySort by remember { mutableStateOf(false) }
    val visible = tasks.filter { task ->
        (search.isBlank() || task.title.contains(search, ignoreCase=true) ||
            task.description.contains(search, ignoreCase=true)) &&
            (category == null || task.category == category!!.name) &&
            when (status) {
                StatusFilter.ALL -> true
                StatusFilter.ACTIVE -> !task.completed
                StatusFilter.DONE -> task.completed
            }
    }.let { base ->
        if (prioritySort) base.sortedWith(compareBy<TaskEntity> { it.completed }.thenBy {
            when (it.priority) { "HIGH" -> 0; "MEDIUM" -> 1; else -> 2 }
        }.thenBy { it.dueAt }) else base.sortedWith(compareBy<TaskEntity> { it.completed }.thenBy { it.dueAt })
    }

    LazyColumn(Modifier.fillMaxSize().testTag("tasks_screen"), contentPadding=PaddingValues(start=22.dp, end=22.dp,
        top=22.dp, bottom=26.dp), verticalArrangement=Arrangement.spacedBy(11.dp)) {
        item {
            SmallEyebrow("ОРГАНИЗУЙ СВОЙ ДЕНЬ", color=TB.Lime)
            Spacer(Modifier.height(8.dp))
            Text("Мои задачи", style=MaterialTheme.typography.headlineLarge,
                color=MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(7.dp))
            Text("${tasks.size} задач • ${tasks.count { it.completed }} завершено",
                color=MaterialTheme.colorScheme.onSurfaceVariant, fontSize=13.sp)
        }
        item {
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value=search, onValueChange={ search=it },
                modifier=Modifier.fillMaxWidth().testTag("task_search"), singleLine=true,
                keyboardOptions=KeyboardOptions(imeAction=ImeAction.Search),
                keyboardActions=KeyboardActions(onSearch={ keyboardController?.hide() }),
                placeholder={ Text("Найти задачу...", fontSize=14.sp) },
                leadingIcon={ Icon(Icons.Outlined.Search, null, tint=MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingIcon={ if(search.isNotBlank()) IconButton(onClick={search=""}) {
                    Icon(Icons.Outlined.Close, "Очистить поиск")
                } },
                shape=RoundedCornerShape(18.dp),
                colors=OutlinedTextFieldDefaults.colors(
                    focusedContainerColor=MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor=MaterialTheme.colorScheme.surface,
                    focusedBorderColor=TB.Lime,
                    unfocusedBorderColor=MaterialTheme.colorScheme.outline.copy(alpha=.5f)))
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                StatusFilter.entries.forEach { choice ->
                    FilterPill(choice.text, status==choice) { status=choice }
                }
            }
        }
        item {
            Spacer(Modifier.height(2.dp))
            CategoryFilter(category, { category = it })
        }
        item {
            Spacer(Modifier.height(9.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically,
                horizontalArrangement=Arrangement.SpaceBetween) {
                Text("Найдено: ${visible.size}", fontWeight=FontWeight.SemiBold,
                    color=MaterialTheme.colorScheme.onSurfaceVariant, fontSize=12.sp)
                TextButton(onClick={prioritySort=!prioritySort}, contentPadding=PaddingValues(3.dp)) {
                    Icon(Icons.Outlined.SwapVert, null, modifier=Modifier.size(17.dp), tint=TB.Lime)
                    Spacer(Modifier.width(5.dp))
                    Text(if (prioritySort) "По приоритету" else "По дате",
                        fontSize=12.sp, color=TB.Lime)
                }
            }
        }
        if (visible.isEmpty()) {
            item { EmptyTasks("Ничего не найдено", "Измени фильтры или добавь новую задачу.", onNew) }
        } else {
            items(visible, key={it.id}) { task ->
                TaskItem(task, {onToggle(task)}, {onEdit(task)}, {onDelete(task)})
            }
        }
    }
}
