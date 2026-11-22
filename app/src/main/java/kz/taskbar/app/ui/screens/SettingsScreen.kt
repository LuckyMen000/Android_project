package kz.taskbar.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.taskbar.app.data.TaskEntity
import kz.taskbar.app.ui.components.*
import kz.taskbar.app.ui.theme.TB

@Composable
fun SettingsScreen(isDark: Boolean, tasks: List<TaskEntity>,
                   onDarkChange: (Boolean)->Unit, onClearCompleted: ()->Unit) {
    var confirmClear by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal=22.dp)) {
        Spacer(Modifier.height(23.dp))
        SmallEyebrow("ПОД СЕБЯ", color=TB.Lime)
        Spacer(Modifier.height(8.dp))
        Text("Настройки", style=MaterialTheme.typography.headlineLarge,
            color=MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(7.dp))
        Text("Пространство для твоего фокуса.", color=MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize=13.sp)
        Spacer(Modifier.height(28.dp))

        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=.5f), RoundedCornerShape(26.dp))
            .padding(20.dp), verticalAlignment=Alignment.CenterVertically) {
            Box(Modifier.size(59.dp).clip(RoundedCornerShape(19.dp))
                .background(TB.Lime), contentAlignment=Alignment.Center) {
                Icon(Icons.Outlined.PersonOutline, null, tint=TB.LimeInk,
                    modifier=Modifier.size(31.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Моё пространство", color=MaterialTheme.colorScheme.onSurface,
                    fontSize=17.sp, fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text("Локальный аккаунт · без регистрации", fontSize=11.sp,
                    color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(31.dp))
        SmallEyebrow("ВНЕШНИЙ ВИД")
        Spacer(Modifier.height(11.dp))
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(23.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=.5f), RoundedCornerShape(23.dp))
            .clickable { onDarkChange(!isDark) }.padding(18.dp),
            verticalAlignment=Alignment.CenterVertically) {
            Icon(if(isDark) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                null, tint=TB.Violet, modifier=Modifier.size(24.dp))
            Spacer(Modifier.width(15.dp))
            Column(Modifier.weight(1f)) {
                Text("Тёмная тема", color=MaterialTheme.colorScheme.onSurface,
                    fontWeight=FontWeight.SemiBold, fontSize=14.sp)
                Spacer(Modifier.height(4.dp))
                Text(if(isDark) "Включена" else "Выключена", fontSize=11.sp,
                    color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked=isDark, onCheckedChange=onDarkChange,
                colors=SwitchDefaults.colors(checkedThumbColor=TB.LimeInk,
                    checkedTrackColor=TB.Lime))
        }
        Spacer(Modifier.height(28.dp))
        SmallEyebrow("ДАННЫЕ")
        Spacer(Modifier.height(11.dp))
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(23.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=.5f), RoundedCornerShape(23.dp))
            .clickable(enabled=tasks.any {it.completed}) { confirmClear=true }
            .padding(19.dp), verticalAlignment=Alignment.CenterVertically) {
            Icon(Icons.Outlined.DeleteSweep, null, tint=TB.Coral, modifier=Modifier.size(24.dp))
            Spacer(Modifier.width(15.dp))
            Column(Modifier.weight(1f)) {
                Text("Очистить выполненные", color=MaterialTheme.colorScheme.onSurface,
                    fontSize=14.sp, fontWeight=FontWeight.SemiBold)
                Spacer(Modifier.height(5.dp))
                Text("Удалить ${tasks.count {it.completed}} завершённых задач", fontSize=11.sp,
                    color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(29.dp))
        SmallEyebrow("О ПРИЛОЖЕНИИ")
        Spacer(Modifier.height(12.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(23.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=.5f), RoundedCornerShape(23.dp))
            .padding(20.dp)) {
            Text("TASKBAR / 01", color=TB.Lime, fontWeight=FontWeight.Black,
                letterSpacing=1.5.sp, fontSize=13.sp)
            Spacer(Modifier.height(10.dp))
            Text("Управляй задачами, а не позволяй задачам управлять тобой.",
                color=MaterialTheme.colorScheme.onSurface, fontSize=14.sp, lineHeight=21.sp)
            Spacer(Modifier.height(13.dp))
            Text("Версия 1.0.0 • Android • Kotlin + Jetpack Compose",
                color=MaterialTheme.colorScheme.onSurfaceVariant, fontSize=10.sp)
            Spacer(Modifier.height(6.dp))
            Text("Задачи хранятся локально. Цитата дня загружается по сети при наличии интернета.",
                color=MaterialTheme.colorScheme.onSurfaceVariant, fontSize=10.sp, lineHeight=16.sp)
        }
        Spacer(Modifier.height(28.dp))
    }
    if (confirmClear) AlertDialog(
        onDismissRequest={ confirmClear=false },
        title={Text("Удалить выполненные задачи?")},
        text={Text("Это действие нельзя отменить. Незавершённые задачи останутся на месте.")},
        confirmButton={TextButton(onClick={onClearCompleted();confirmClear=false}) {
            Text("Удалить", color=MaterialTheme.colorScheme.error)
        }},
        dismissButton={TextButton(onClick={confirmClear=false}) {Text("Отмена")}},
    )
}
