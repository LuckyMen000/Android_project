package kz.taskbar.app.debug

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.taskbar.app.TaskBarApplication
import kz.taskbar.app.diagnostics.TaskBarLog
import kz.taskbar.app.domain.ProgressCalculator
import kz.taskbar.app.ui.theme.TB
import kz.taskbar.app.ui.theme.TaskBarTheme
import kotlinx.coroutines.launch
import java.net.URL

/** Available ONLY in the debug source set; release APK never contains this Activity. */
class DebugLabActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TaskBarLog.i(TaskBarLog.LAB, "Debug Lab opened")
        setContent {
            TaskBarTheme(darkTheme = true) {
                DebugLabScreen(onBack = { finish() })
            }
        }
    }
}

/** Intentional integer-division bug. Keep it only in the debug source set. */
internal fun calculateBuggyPercent(completed: Int, total: Int): Int {
    val safeTotal = total.coerceAtLeast(1)
    val result = (completed / safeTotal) * 100 // BREAKPOINT: 2 / 6 => 0, not 33
    return result
}

private fun deliberateNullPointer() {
    val taskName: String? = null
    taskName!!.length // Expected NullPointerException, caught by the laboratory.
}

private fun deliberateIndexOutOfBounds() {
    val entries = listOf("Работа", "Учёба")
    entries[5] // Expected IndexOutOfBoundsException, caught by the laboratory.
}

private fun deliberateNetworkOnMainThread() {
    // Make the error reproducible even if the current StrictMode policy is permissive.
    val previousPolicy = StrictMode.getThreadPolicy()
    try {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder(previousPolicy)
                .detectNetwork()
                .penaltyDeathOnNetwork()
                .build()
        )
        URL("https://dummyjson.com/quotes/random").openStream().use { it.read() }
    } finally {
        StrictMode.setThreadPolicy(previousPolicy)
    }
}

@Composable
private fun DebugLabScreen(onBack: () -> Unit) {
    val localContext = LocalContext.current
    val appActivity = localContext as? DebugLabActivity
    val app = localContext.applicationContext as TaskBarApplication
    val scope = rememberCoroutineScope()
    var resultText by remember { mutableStateOf("Готово к проверке. Выберите действие ниже.") }
    var brokenLayout by remember { mutableStateOf(true) }
    val broken = calculateBuggyPercent(completed = 2, total = 6)
    val corrected = ProgressCalculator.completionPercent(completed = 2, total = 6)

    fun runCrash(name: String, block: () -> Unit) {
        try {
            block()
            resultText = "$name: исключение не возникло. Проверьте условия запуска."
            TaskBarLog.w(TaskBarLog.LAB, resultText)
        } catch (error: Exception) {
            TaskBarLog.e(TaskBarLog.LAB, "EXPECTED $name: ${error.javaClass.simpleName}", error)
            resultText = "$name: ${error.javaClass.simpleName}. Stack trace в Logcat."
        }
    }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .statusBarsPadding().navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 21.dp, vertical = 22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("debug_back")) {
                Icon(Icons.Outlined.ArrowBack, "Назад", tint = TB.Lime)
            }
            Spacer(Modifier.width(8.dp))
            Text("TASKBAR / LAB", fontSize = 13.sp, color = TB.Lime,
                fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        }
        Spacer(Modifier.height(24.dp))
        Text("Тестируем.\nИсправляем.", fontSize = 35.sp, lineHeight = 40.sp,
            fontWeight = FontWeight.Black, color = Color.White)
        Spacer(Modifier.height(10.dp))
        Text("РГР №3  ·  ЗАДАНИЯ 01—04", color = TB.Muted, fontSize = 12.sp)
        Spacer(Modifier.height(19.dp))
        Card(colors = CardDefaults.cardColors(containerColor = TB.Panel2),
            shape = RoundedCornerShape(18.dp)) {
            Text(resultText, Modifier.fillMaxWidth().padding(17.dp).testTag("debug_lab_result"),
                color = TB.Lime, fontSize = 12.sp, lineHeight = 18.sp)
        }
        Spacer(Modifier.height(18.dp))

        LabSection("01", "LOGCAT", "Проверьте уровни и фильтрацию тегов TB_*") {
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf("DEBUG", "INFO", "WARN", "ERROR").forEach { level ->
                    OutlinedButton(
                        onClick = {
                            when (level) {
                                "DEBUG" -> TaskBarLog.d(TaskBarLog.LAB, "Sample debug event")
                                "INFO" -> TaskBarLog.i(TaskBarLog.LAB, "Sample info event")
                                "WARN" -> TaskBarLog.w(TaskBarLog.LAB, "Sample warning event")
                                else -> TaskBarLog.e(TaskBarLog.LAB, "Sample error event")
                            }
                            resultText = "Logcat: TB_LAB / $level"
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 10.dp)
                    ) {
                        Text(level, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        LabSection("02", "DEBUGGER", "Намеренная ошибка целочисленного деления") {
            Text("Выполнено: 2 из 6 задач", color = Color.White, fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                Metric("ОШИБКА", "$broken%", TB.Coral, Modifier.weight(1f))
                Metric("ИСПРАВЛЕНО", "$corrected%", TB.Lime, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            LabButton("Запустить расчёт", modifier = Modifier.fillMaxWidth()) {
                val sampleBuggy = calculateBuggyPercent(2, 6)
                val sampleFixed = ProgressCalculator.completionPercent(2, 6)
                TaskBarLog.w(TaskBarLog.LAB, "Progress bug=$sampleBuggy fixed=$sampleFixed")
                resultText = "Debugger: неправильный $sampleBuggy%, правильный $sampleFixed%."
            }
        }
        LabSection("03", "EXCEPTIONS", "Ошибки изолированы: приложение не закрывается") {
            LabButton("NullPointerException") {
                runCrash("NPE", ::deliberateNullPointer)
            }
            Spacer(Modifier.height(8.dp))
            LabButton("IndexOutOfBoundsException") {
                runCrash("Index", ::deliberateIndexOutOfBounds)
            }
            Spacer(Modifier.height(8.dp))
            LabButton("NetworkOnMainThreadException") {
                runCrash("Network", ::deliberateNetworkOnMainThread)
            }
            Spacer(Modifier.height(8.dp))
            LabButton("Правильный сетевой запрос (корутина)") {
                scope.launch {
                    val quote = app.quoteRepository.getQuote() // Retrofit + Dispatchers.IO
                    TaskBarLog.i(TaskBarLog.NETWORK, "Safe quote loaded; online=${quote.fromNetwork}")
                    resultText = if (quote.fromNetwork) "Сеть: HTTP-запрос успешен."
                        else "Сеть недоступна: включён безопасный офлайн-режим."
                }
            }
        }
        LabSection("04", "INSPECTORS", "Layout / Database / Network") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Дефект вёрстки", color = Color.White, modifier = Modifier.weight(1f))
                Switch(checked = brokenLayout, onCheckedChange = { brokenLayout = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = TB.Lime))
            }
            Spacer(Modifier.height(9.dp))
            if (brokenLayout) {
                Box(
                    Modifier.fillMaxWidth().height(76.dp).clip(RoundedCornerShape(16.dp))
                        .background(TB.Panel2).testTag("layout_defect")
                ) {
                    Text("Перекрытый заголовок", Modifier.align(Alignment.Center), color = Color.White)
                    Text("ТЕКСТ НА ТЕКСТЕ", Modifier.align(Alignment.Center), color = TB.Coral)
                }
            } else {
                Row(
                    Modifier.fillMaxWidth().height(76.dp).clip(RoundedCornerShape(16.dp))
                        .background(TB.Panel2).padding(13.dp).testTag("layout_fixed"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.CheckCircle, null, tint = TB.Lime)
                    Spacer(Modifier.width(10.dp))
                    Text("Перекрытие исправлено", color = Color.White)
                }
            }
            Spacer(Modifier.height(11.dp))
            LabButton("Проверить таблицу Room (tasks)") {
                scope.launch {
                    val count = app.database.tasks().totalCount()
                    TaskBarLog.i(TaskBarLog.DATABASE, "Database Inspector check count=$count")
                    resultText = "Room: в таблице tasks $count записей."
                }
            }
            Spacer(Modifier.height(8.dp))
            LabButton("Выполнить HTTP-запрос") {
                scope.launch {
                    val quote = app.quoteRepository.getQuote()
                    TaskBarLog.i(TaskBarLog.NETWORK, "Inspector request online=${quote.fromNetwork}")
                    resultText = "Network Inspector: ${if (quote.fromNetwork) "проверьте запрос" else "офлайн-резерв"}."
                }
            }
        }
        LabSection("13", "MEMORY & STRICTMODE", "Управляемый пример утечки и её устранения") {
            Text("LeakCanary автоматически наблюдает уничтоженные Activity в debug-сборке.",
                color = TB.Muted, fontSize = 12.sp)
            Spacer(Modifier.height(9.dp))
            LabButton("Создать демонстрационную утечку Activity") {
                val activity = appActivity ?: return@LabButton
                DebugLeakScenario.capture(activity)
                resultText = "Activity удерживается singleton. Закройте Lab и проверьте LeakCanary."
                TaskBarLog.w(TaskBarLog.LAB, "Leak demo: Activity retained (debug-only)")
            }
            Spacer(Modifier.height(8.dp))
            LabButton("Исправить: освободить Activity") {
                DebugLeakScenario.release()
                resultText = "Ссылка на Activity освобождена. Повторите профилирование."
                TaskBarLog.i(TaskBarLog.LAB, "Leak demo fixed: Activity reference released")
            }
            Spacer(Modifier.height(8.dp))
            LabButton("Проверить StrictMode") {
                TaskBarLog.i(TaskBarLog.LAB, "StrictMode policy enabled in TaskBarApplication")
                resultText = "StrictMode включён. Проверьте Logcat по StrictMode и TB_APP."
            }
        }
        Spacer(Modifier.height(25.dp))
        Text("Только в DEBUG • Не изменяет пользовательские задачи при моделировании ошибок",
            fontSize = 10.sp, lineHeight = 15.sp, color = TB.Muted)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LabSection(number: String, title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(top = 12.dp)
            .clip(RoundedCornerShape(23.dp)).background(MaterialTheme.colorScheme.surface)
            .border(1.dp, TB.Border, RoundedCornerShape(23.dp))
            .padding(17.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(number, color = TB.Lime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(9.dp))
            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(5.dp))
        Text(subtitle, color = TB.Muted, fontSize = 11.sp)
        Spacer(Modifier.height(17.dp))
        content()
    }
}

@Composable
private fun LabButton(label: String, modifier: Modifier = Modifier, action: () -> Unit) {
    Row(modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp))
        .background(TB.Panel2).clickable(onClick = action).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f), color = Color.White,
            fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Icon(Icons.Outlined.ArrowForward, null, tint = TB.Lime, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun Metric(title: String, value: String, tint: Color, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(15.dp)).background(TB.Panel2).padding(14.dp)) {
        Text(title, color = TB.Muted, fontSize = 10.sp)
        Spacer(Modifier.height(6.dp))
        Text(value, color = tint, fontSize = 24.sp, fontWeight = FontWeight.Black)
    }
}
