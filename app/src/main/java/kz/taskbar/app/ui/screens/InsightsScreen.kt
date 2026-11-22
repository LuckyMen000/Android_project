package kz.taskbar.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlin.math.max

@Composable
fun InsightsScreen(tasks: List<TaskEntity>) {
    val done = tasks.count { it.completed }
    val progress = if(tasks.isEmpty()) 0f else done.toFloat()/tasks.size
    val today = remember { startOfDay(System.currentTimeMillis()) }
    val week = remember(today) { (-6..0).map { shiftDays(today,it) } }
    val dailyDone = week.map { day -> tasks.count { it.completedAt?.let { date -> isSameDay(date, day) } == true } }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal=22.dp)) {
        Spacer(Modifier.height(23.dp))
        SmallEyebrow("ТВОИ РЕЗУЛЬТАТЫ", color=TB.Lime)
        Spacer(Modifier.height(8.dp))
        Text("Аналитика", style=MaterialTheme.typography.headlineLarge,
            color=MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(8.dp))
        Text("Отслеживай движение к своим целям.", fontSize=13.sp,
            color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(25.dp))

        Row(Modifier.fillMaxWidth().height(185.dp).clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF333754),Color(0xFF171B31))))
            .padding(22.dp), verticalAlignment=Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                SmallEyebrow("ОБЩИЙ ПРОГРЕСС", color=TB.Lime)
                Spacer(Modifier.height(14.dp))
                Text("${(progress*100).toInt()}%", fontSize=47.sp, fontWeight=FontWeight.Black,
                    color=Color.White)
                Spacer(Modifier.height(6.dp))
                Text("$done / ${tasks.size} выполнено", color=Color(0xFFC3C8D6), fontSize=12.sp)
            }
            ProgressRing(progress, Modifier.size(103.dp), label="✓")
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            TaskStat("Всего задач", tasks.size, TB.Blue,
                Icons.Outlined.FormatListBulleted, Modifier.weight(1f))
            TaskStat("Активных", tasks.size-done, TB.Coral,
                Icons.Outlined.HourglassTop, Modifier.weight(1f))
        }
        Spacer(Modifier.height(31.dp))
        SectionHeading("Активность за 7 дней")
        Spacer(Modifier.height(14.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=.45f), RoundedCornerShape(25.dp))
            .padding(horizontal=21.dp, vertical=22.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Icon(Icons.Outlined.TrendingUp, null, tint=TB.Lime, modifier=Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Выполненные задачи", color=MaterialTheme.colorScheme.onSurface,
                    fontSize=13.sp, fontWeight=FontWeight.SemiBold)
            }
            Spacer(Modifier.height(22.dp))
            Row(Modifier.fillMaxWidth().height(140.dp), verticalAlignment=Alignment.Bottom,
                horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                val peak=max(1,dailyDone.maxOrNull() ?: 1)
                week.forEachIndexed { index, day ->
                    val amount=dailyDone[index]
                    val height=if (amount==0) 5.dp else (105f * amount / peak).dp
                    Column(Modifier.weight(1f).fillMaxHeight(), horizontalAlignment=Alignment.CenterHorizontally,
                        verticalArrangement=Arrangement.Bottom) {
                        Text(if(amount==0) "" else "$amount", fontSize=10.sp, color=TB.Lime,
                            fontWeight=FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Box(Modifier.fillMaxWidth(.7f).height(height)
                            .clip(RoundedCornerShape(topStart=8.dp, topEnd=8.dp, bottomStart=4.dp, bottomEnd=4.dp))
                            .background(if(isSameDay(day,today)) TB.Lime else TB.Violet.copy(alpha=.65f)))
                        Spacer(Modifier.height(8.dp))
                        Text(SimpleDateFormat("EE",Locale("ru","RU")).format(Date(day))
                            .replace(".","").take(2).uppercase(Locale("ru","RU")),
                            fontSize=10.sp, color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Spacer(Modifier.height(30.dp))
        SectionHeading("По направлениям")
        Spacer(Modifier.height(13.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=.45f), RoundedCornerShape(25.dp))
            .padding(20.dp), verticalArrangement=Arrangement.spacedBy(18.dp)) {
            TaskCategory.entries.forEach { category ->
                val catTasks=tasks.filter { it.category==category.name }
                val catDone=catTasks.count { it.completed }
                val ratio=if (catTasks.isEmpty()) 0f else catDone.toFloat()/catTasks.size
                Column {
                    Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically) {
                        Box(Modifier.size(9.dp).background(tone(category), androidx.compose.foundation.shape.CircleShape))
                        Spacer(Modifier.width(9.dp))
                        Text(category.title, color=MaterialTheme.colorScheme.onSurface,
                            fontSize=13.sp, fontWeight=FontWeight.SemiBold)
                        Spacer(Modifier.weight(1f))
                        Text("$catDone/${catTasks.size}", fontSize=12.sp,
                            color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(9.dp))
                    Box(Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)) {
                        Box(Modifier.fillMaxWidth(ratio).fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp)).background(tone(category)))
                    }
                }
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}
