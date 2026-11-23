package kz.taskbar.app.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.taskbar.app.data.TaskEntity
import kz.taskbar.app.domain.TaskCategory
import kz.taskbar.app.domain.TaskDraft
import kz.taskbar.app.domain.TaskPriority
import kz.taskbar.app.domain.TaskValidator
import kz.taskbar.app.domain.timeToday
import kz.taskbar.app.ui.theme.TB
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorSheet(task: TaskEntity?, onDismiss: () -> Unit, onSave: (TaskDraft) -> Unit) {
    val context = LocalContext.current
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by rememberSaveable(task?.id) { mutableStateOf(task?.title.orEmpty()) }
    var description by rememberSaveable(task?.id) { mutableStateOf(task?.description.orEmpty()) }
    var category by rememberSaveable(task?.id) { mutableStateOf(TaskCategory.fromValue(task?.category ?: "WORK")) }
    var priority by rememberSaveable(task?.id) { mutableStateOf(TaskPriority.fromValue(task?.priority ?: "MEDIUM")) }
    var dueAt by rememberSaveable(task?.id) { mutableLongStateOf(task?.dueAt ?: timeToday(18)) }
    var showValidation by rememberSaveable(task?.id) { mutableStateOf(false) }
    val validation = TaskValidator.validate(title, description)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheet,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(Modifier.padding(top=12.dp,bottom=12.dp).size(width=37.dp,height=5.dp)
                .clip(RoundedCornerShape(3.dp)).background(MaterialTheme.colorScheme.outline))
        }) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding().imePadding()
            .verticalScroll(rememberScrollState())
            .padding(start=23.dp,end=23.dp,bottom=24.dp)) {
            SmallEyebrow(if(task==null) "НОВАЯ ЦЕЛЬ" else "РЕДАКТИРОВАНИЕ", color=TB.Lime)
            Spacer(Modifier.height(8.dp))
            Text(if(task==null) "Новая задача" else "Изменить задачу",
                color=MaterialTheme.colorScheme.onSurface,
                style=MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(19.dp))

            SmallEyebrow("НАЗВАНИЕ")
            Spacer(Modifier.height(9.dp))
            OutlinedTextField(value=title,
                onValueChange={ if(it.length<=TaskValidator.MAX_TITLE) title=it },
                modifier=Modifier.fillMaxWidth().testTag("task_title_input"), singleLine=true,
                placeholder={Text("Что нужно сделать?")},
                isError=showValidation && validation!=null,
                shape=RoundedCornerShape(17.dp),
                colors=editorFieldColors())
            Spacer(Modifier.height(15.dp))
            SmallEyebrow("ОПИСАНИЕ")
            Spacer(Modifier.height(9.dp))
            OutlinedTextField(value=description,
                onValueChange={ if(it.length<=TaskValidator.MAX_DESCRIPTION) description=it },
                modifier=Modifier.fillMaxWidth().testTag("task_description_input").heightIn(min=102.dp), minLines=3,
                placeholder={Text("Добавь детали, если нужно...")},
                shape=RoundedCornerShape(17.dp), colors=editorFieldColors())

            Spacer(Modifier.height(21.dp))
            SmallEyebrow("КАТЕГОРИЯ")
            Spacer(Modifier.height(11.dp))
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                TaskCategory.entries.forEach { value ->
                    FilterPill(value.title, value==category, tone(value)) { category=value }
                }
            }
            Spacer(Modifier.height(21.dp))
            SmallEyebrow("ПРИОРИТЕТ")
            Spacer(Modifier.height(11.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                TaskPriority.entries.forEach { value ->
                    val accent = when(value) {
                        TaskPriority.LOW -> TB.Blue
                        TaskPriority.MEDIUM -> TB.Violet
                        TaskPriority.HIGH -> TB.Coral
                    }
                    Box(Modifier.weight(1f)) {
                        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp))
                            .background(if(value==priority) accent else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if(value==priority) accent else MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(13.dp))
                            .then(Modifier)) {
                            TextButton(onClick={priority=value}, modifier=Modifier.fillMaxWidth(),
                                contentPadding=PaddingValues(horizontal=5.dp,vertical=7.dp)) {
                                Text(value.title, fontSize=12.sp, fontWeight=FontWeight.Bold,
                                    color=if(value==priority) TB.LimeInk else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(21.dp))
            SmallEyebrow("КОГДА ВЫПОЛНИТЬ")
            Spacer(Modifier.height(11.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                EditorDateButton(Icons.Outlined.CalendarMonth,
                    SimpleDateFormat("d MMM yyyy", Locale("ru","RU")).format(Date(dueAt)),
                    Modifier.weight(1.3f)) {
                    val cal=Calendar.getInstance().apply { timeInMillis=dueAt }
                    DatePickerDialog(context, { _, y, m, d ->
                        val next=Calendar.getInstance().apply { timeInMillis=dueAt }
                        next.set(Calendar.YEAR,y)
                        next.set(Calendar.MONTH,m)
                        next.set(Calendar.DAY_OF_MONTH,d)
                        dueAt=next.timeInMillis
                    },cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
                }
                EditorDateButton(Icons.Outlined.Schedule,
                    SimpleDateFormat("HH:mm",Locale("ru","RU")).format(Date(dueAt)),
                    Modifier.weight(.8f)) {
                    val cal=Calendar.getInstance().apply { timeInMillis=dueAt }
                    TimePickerDialog(context, { _, h, m ->
                        val next=Calendar.getInstance().apply { timeInMillis=dueAt }
                        next.set(Calendar.HOUR_OF_DAY,h)
                        next.set(Calendar.MINUTE,m)
                        dueAt=next.timeInMillis
                    },cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true).show()
                }
            }
            if (showValidation && validation!=null) {
                Spacer(Modifier.height(12.dp))
                Text(validation, fontSize=12.sp, color=MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(27.dp))
            PrimaryAction(if(task==null) "Создать задачу" else "Сохранить изменения", onClick={
                showValidation=true
                if(validation==null) {
                    onSave(TaskDraft(id=task?.id ?: 0L, title=title, description=description,
                        category=category, priority=priority, dueAt=dueAt))
                }
            }, modifier=Modifier.fillMaxWidth().testTag("task_save_button"), icon=true)
        }
    }
}

@Composable
private fun editorFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor=MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor=MaterialTheme.colorScheme.surfaceVariant,
    focusedBorderColor=TB.Lime,
    unfocusedBorderColor=MaterialTheme.colorScheme.outline.copy(alpha=.4f))

@Composable
private fun EditorDateButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String,
                             modifier: Modifier=Modifier, onClick: ()->Unit) {
    TextButton(onClick=onClick, modifier=modifier.clip(RoundedCornerShape(15.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant).height(51.dp),
        contentPadding=PaddingValues(horizontal=9.dp)) {
        Icon(icon,null,tint=TB.Lime,modifier=Modifier.size(17.dp))
        Spacer(Modifier.width(7.dp))
        Text(text, fontSize=12.sp, maxLines=1, color=MaterialTheme.colorScheme.onSurface,
            fontWeight=FontWeight.SemiBold)
    }
}
