package kz.taskbar.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.taskbar.app.data.DailyQuote
import kz.taskbar.app.data.TaskEntity
import kz.taskbar.app.domain.TaskCategory
import kz.taskbar.app.domain.isSameDay
import kz.taskbar.app.domain.shiftDays
import kz.taskbar.app.domain.startOfDay
import kz.taskbar.app.ui.components.*
import kz.taskbar.app.ui.theme.TB
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(tasks: List<TaskEntity>, quote: DailyQuote, onAll: () -> Unit,
               onNew: () -> Unit, onToggle: (TaskEntity) -> Unit,
               onEdit: (TaskEntity) -> Unit, onDelete: (TaskEntity) -> Unit,
               onQuoteRefresh: () -> Unit) {
    val today = remember { startOfDay(System.currentTimeMillis()) }
    var selectedDay by rememberSaveable { mutableLongStateOf(today) }
    var category by remember { mutableStateOf<TaskCategory?>(null) }
    val completed = tasks.count { it.completed }
    val dueTasks = tasks.filter { isSameDay(it.dueAt, selectedDay) &&
        (category == null || it.category == category!!.name) }
    val selectedTitle = if (selectedDay == today) "На сегодня" else "На ${readableDay(selectedDay)}"

    Column(Modifier.fillMaxSize().testTag("home_screen").verticalScroll(rememberScrollState())
        .padding(horizontal = 22.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Spacer(Modifier.height(16.dp))
        BrandHeader()
        Spacer(Modifier.height(31.dp))
        SmallEyebrow("ТВОЙ ДЕНЬ • ТВОЙ РИТМ", color = TB.Lime)
        Spacer(Modifier.height(9.dp))
        Text("Сделай день\nпо-своему.", color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(11.dp))
        Text("Маленькие шаги сегодня — большие\nрезультаты завтра.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(28.dp))
        ProgressHero(completed, tasks.size)
        Spacer(Modifier.height(13.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TaskStat("В процессе", tasks.count { !it.completed }, TB.Violet,
                Icons.Outlined.Bolt, Modifier.weight(1f))
            TaskStat("Выполнено", completed, TB.Lime,
                Icons.Outlined.TaskAlt, Modifier.weight(1f))
        }
        Spacer(Modifier.height(32.dp))
        SectionHeading("Ближайшие дни")
        Spacer(Modifier.height(15.dp))
        WeekStrip(today, selectedDay, tasks, onSelect = { selectedDay = it })
        Spacer(Modifier.height(31.dp))
        SectionHeading(selectedTitle, "Все задачи", onAll)
        Spacer(Modifier.height(14.dp))
        CategoryFilter(category, onSelect = { category = it })
        Spacer(Modifier.height(15.dp))
        if (dueTasks.isEmpty()) {
            EmptyTasks("Свободное время — тоже план", "На выбранную дату задач пока нет.", onNew)
        } else {
            dueTasks.sortedWith(compareBy<TaskEntity> { it.completed }.thenBy { it.dueAt })
                .take(6).forEach { task ->
                    TaskItem(task, { onToggle(task) }, { onEdit(task) }, { onDelete(task) })
                    Spacer(Modifier.height(10.dp))
                }
        }
        Spacer(Modifier.height(19.dp))
        QuotePanel(quote, onQuoteRefresh)
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun BrandHeader() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(37.dp).clip(RoundedCornerShape(12.dp))
            .background(TB.Lime), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Segment, null, tint = TB.LimeInk, modifier = Modifier.size(25.dp))
        }
        Spacer(Modifier.width(11.dp))
        Text("TASKBAR", color = MaterialTheme.colorScheme.onBackground,
            fontSize = 19.sp, fontWeight = FontWeight.Black, letterSpacing = 1.7.sp)
        Spacer(Modifier.weight(1f))
        Box(Modifier.clip(RoundedCornerShape(13.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=.5f), RoundedCornerShape(13.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp)) {
            Text(SimpleDateFormat("dd MMM", Locale("ru", "RU")).format(Date()).uppercase(Locale("ru", "RU")),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProgressHero(done: Int, total: Int) {
    val progress = if (total == 0) 0f else done.toFloat() / total
    Box(Modifier.fillMaxWidth().height(203.dp).clip(RoundedCornerShape(28.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF31364D),
            Color(0xFF232741), Color(0xFF171D31))))) {
        Canvas(Modifier.fillMaxSize()) {
            val radius = size.minDimension * .64f
            drawCircle(color = Color.White.copy(alpha=.035f), radius=radius,
                center=androidx.compose.ui.geometry.Offset(size.width*.98f,size.height*.10f),
                style=Stroke(width=2.dp.toPx()))
            drawCircle(color = TB.Lime.copy(alpha=.06f), radius=radius*.7f,
                center=androidx.compose.ui.geometry.Offset(size.width*1.06f,size.height*.2f))
        }
        Row(Modifier.fillMaxSize().padding(23.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("ТВОЙ ПРОГРЕСС", fontSize = 10.sp, letterSpacing = 1.4.sp,
                    fontWeight = FontWeight.Bold, color = TB.Lime)
                Spacer(Modifier.height(12.dp))
                Text("Ты в хорошем\nтемпе!", fontSize = 24.sp, lineHeight = 29.sp,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(14.dp))
                Text("$done из $total задач выполнено", fontSize = 11.sp,
                    color = Color(0xFFC3C8D6))
            }
            ProgressRing(progress, Modifier.size(113.dp))
        }
    }
}

@Composable
private fun WeekStrip(today: Long, selected: Long, tasks: List<TaskEntity>,
                      onSelect: (Long) -> Unit) {
    val days = remember(today) { (-2..4).map { shiftDays(today, it) } }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        days.forEach { day ->
            val selectedDay = isSameDay(day, selected)
            val hasTask = tasks.any { isSameDay(it.dueAt, day) }
            val label = SimpleDateFormat("EE", Locale("ru", "RU"))
                .format(Date(day)).replace(".", "").uppercase(Locale("ru", "RU"))
            val number = SimpleDateFormat("d", Locale("ru", "RU")).format(Date(day))
            Column(Modifier.weight(1f).height(91.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(if (selectedDay) TB.Lime else MaterialTheme.colorScheme.surface)
                .border(1.dp, if (selectedDay) TB.Lime else MaterialTheme.colorScheme.outline.copy(alpha=.5f),
                    RoundedCornerShape(17.dp))
                .clickable { onSelect(day) }.padding(vertical = 11.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween) {
                Text(label.take(2), fontSize = 9.sp, fontWeight = FontWeight.Bold,
                    color = if (selectedDay) TB.LimeInk else MaterialTheme.colorScheme.onSurfaceVariant)
                Text(number, fontSize = 19.sp, fontWeight = FontWeight.Black,
                    color = if (selectedDay) TB.LimeInk else MaterialTheme.colorScheme.onSurface)
                Box(Modifier.size(5.dp).background(if (hasTask) {
                    if (selectedDay) TB.LimeInk else TB.Lime
                } else Color.Transparent, androidx.compose.foundation.shape.CircleShape))
            }
        }
    }
}

@Composable
private fun QuotePanel(quote: DailyQuote, onRefresh: () -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(23.dp))
        .background(MaterialTheme.colorScheme.surface)
        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .45f), RoundedCornerShape(23.dp))
        .padding(21.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.AutoAwesome, null, tint = TB.Violet, modifier=Modifier.size(19.dp))
            Spacer(Modifier.width(8.dp))
            SmallEyebrow("Мысль дня", color = TB.Violet)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onRefresh, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Outlined.Refresh, "Обновить цитату", tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier=Modifier.size(19.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("“${quote.text}”", fontSize=17.sp, lineHeight=24.sp, fontWeight=FontWeight.SemiBold,
            color=MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(11.dp))
        Text("— ${quote.author}", fontSize=11.sp,
            color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
