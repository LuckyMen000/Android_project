package kz.taskbar.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.taskbar.app.data.TaskEntity
import kz.taskbar.app.domain.TaskCategory
import kz.taskbar.app.domain.TaskPriority
import kz.taskbar.app.domain.isSameDay
import kz.taskbar.app.domain.shiftDays
import kz.taskbar.app.domain.startOfDay
import kz.taskbar.app.ui.theme.TB
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

fun tone(category: TaskCategory): Color = when (category) {
    TaskCategory.WORK -> TB.Violet
    TaskCategory.STUDY -> TB.Blue
    TaskCategory.PERSONAL -> TB.Coral
    TaskCategory.HEALTH -> TB.Lime
}

fun readableDay(time: Long): String = SimpleDateFormat("d MMM", Locale("ru", "RU")).format(Date(time))
fun readableTime(time: Long): String = SimpleDateFormat("HH:mm", Locale("ru", "RU")).format(Date(time))
fun fullDate(time: Long): String = SimpleDateFormat("d MMMM, EEEE", Locale("ru", "RU")).format(Date(time))

@Composable
fun SmallEyebrow(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(text.uppercase(Locale("ru", "RU")), modifier, color = color,
        style = MaterialTheme.typography.labelSmall)
}

@Composable
fun PrimaryAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
                  enabled: Boolean = true, icon: Boolean = false) {
    val lime = TB.Lime
    Row(modifier = modifier
        .height(56.dp)
        .clip(RoundedCornerShape(19.dp))
        .background(if (enabled) lime else lime.copy(alpha = 0.35f))
        .clickable(enabled = enabled, onClick = onClick)
        .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        Text(text, style = MaterialTheme.typography.titleMedium, color = TB.LimeInk)
        if (icon) {
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Outlined.ArrowForward, null, tint = TB.LimeInk, modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
fun Tag(label: String, color: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier.clip(RoundedCornerShape(9.dp))
        .background(color.copy(alpha = 0.13f)).padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(5.dp).background(color, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp)
    }
}

@Composable
fun TaskItem(task: TaskEntity, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit,
             modifier: Modifier = Modifier) {
    val category = TaskCategory.fromValue(task.category)
    val priority = TaskPriority.fromValue(task.priority)
    val foreground = MaterialTheme.colorScheme.onSurface
    val activeTint = tone(category)
    var menuOpen by remember { mutableStateOf(false) }
    val checkBackground by animateColorAsState(if (task.completed) TB.Lime else Color.Transparent, label = "checkbox")
    Row(modifier = modifier.fillMaxWidth().testTag("task_${task.id}")
        .clip(RoundedCornerShape(22.dp))
        .background(MaterialTheme.colorScheme.surface)
        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .45f), RoundedCornerShape(22.dp))
        .clickable(onClick = onEdit)
        .padding(start = 17.dp, top = 16.dp, bottom = 16.dp, end = 7.dp),
        verticalAlignment = Alignment.Top) {
        Box(Modifier.padding(top = 3.dp).size(25.dp).testTag("toggle_${task.id}")
            .clip(CircleShape)
            .border(1.6.dp, if (task.completed) TB.Lime else activeTint.copy(alpha = .8f), CircleShape)
            .background(checkBackground).clickable(onClick = onToggle)
            .semantics { contentDescription = if (task.completed) "Вернуть задачу в работу" else "Отметить задачу выполненной" },
            contentAlignment = Alignment.Center) {
            if (task.completed) Icon(Icons.Rounded.Check, null, tint = TB.LimeInk, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) {
            Text(task.title, color = if (task.completed) MaterialTheme.colorScheme.onSurfaceVariant else foreground,
                textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (task.description.isNotBlank()) {
                Spacer(Modifier.height(5.dp))
                Text(task.description, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp, lineHeight = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Tag(category.shortName, activeTint)
                Icon(Icons.Outlined.Schedule, null, tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp))
                Text(readableTime(task.dueAt), fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (priority == TaskPriority.HIGH) {
                    Box(Modifier.size(5.dp).background(TB.Coral, CircleShape))
                }
            }
        }
        Box {
            IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(38.dp)) {
                Icon(Icons.Outlined.MoreHoriz, "Действия с задачей", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false },
                containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                DropdownMenuItem(text = { Text("Редактировать") }, onClick = {
                    menuOpen = false; onEdit()
                }, leadingIcon = { Icon(Icons.Outlined.Edit, null) })
                DropdownMenuItem(text = { Text("Удалить", color = MaterialTheme.colorScheme.error) },
                    onClick = { menuOpen = false; onDelete() },
                    leadingIcon = { Icon(Icons.Outlined.DeleteOutline, null,
                        tint = MaterialTheme.colorScheme.error) })
            }
        }
    }
}

@Composable
fun CategoryFilter(selected: TaskCategory?, onSelect: (TaskCategory?) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterPill("Все", selected == null, TB.Lime) { onSelect(null) }
        TaskCategory.entries.forEach { category ->
            FilterPill(category.title, selected == category, tone(category)) { onSelect(category) }
        }
    }
}

@Composable
fun FilterPill(text: String, selected: Boolean, accent: Color = TB.Lime, onClick: () -> Unit) {
    Box(Modifier.clip(RoundedCornerShape(13.dp))
        .background(if (selected) accent else MaterialTheme.colorScheme.surface)
        .border(1.dp, if (selected) accent else MaterialTheme.colorScheme.outline.copy(alpha=.65f),
            RoundedCornerShape(13.dp))
        .clickable(onClick = onClick)
        .padding(horizontal = 16.dp, vertical = 11.dp)) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            color = if (selected) TB.LimeInk else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EmptyTasks(title: String, subtitle: String, onAdd: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 37.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(80.dp).clip(RoundedCornerShape(29.dp))
            .background(TB.Lime.copy(alpha = .10f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.AutoAwesome, null, tint = TB.Lime, modifier = Modifier.size(37.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        TextButton(onClick = onAdd) { Text("+ Добавить задачу", color = TB.Lime, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun ProgressRing(progress: Float, modifier: Modifier = Modifier, accent: Color = TB.Lime,
                 label: String? = null) {
    val anim by animateFloatAsState(progress.coerceIn(0f, 1f), label = "progress")
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 11.dp.toPx()
            drawArc(color = Color.White.copy(alpha = .12f), startAngle = 0f, sweepAngle = 360f,
                useCenter = false, style = Stroke(width = stroke, cap = StrokeCap.Round))
            drawArc(color = accent, startAngle = -90f, sweepAngle = 360f * anim,
                useCenter = false, style = Stroke(width = stroke, cap = StrokeCap.Round))
        }
        Text(label ?: "${(anim * 100).toInt()}%", fontSize = 26.sp,
            fontWeight = FontWeight.Black, color = Color.White)
    }
}

@Composable
fun TaskStat(label: String, number: Int, accent: Color, icon: androidx.compose.ui.graphics.vector.ImageVector,
             modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(24.dp))
        .background(MaterialTheme.colorScheme.surface)
        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .45f), RoundedCornerShape(24.dp))
        .padding(19.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(23.dp))
            Text("↗", fontSize = 18.sp, color = accent.copy(alpha=.7f))
        }
        Spacer(Modifier.height(18.dp))
        Text(number.toString().padStart(2, '0'), fontSize = 33.sp,
            fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(2.dp))
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}

@Composable
fun SectionHeading(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground)
        if (action != null && onAction != null) Text(action,
            color = TB.Lime, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.clip(RoundedCornerShape(10.dp)).clickable(onClick = onAction)
                .padding(horizontal = 4.dp, vertical = 7.dp))
    }
}
