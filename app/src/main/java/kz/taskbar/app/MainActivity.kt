package kz.taskbar.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kz.taskbar.app.data.TaskEntity
import kz.taskbar.app.diagnostics.TaskBarLog
import kz.taskbar.app.ui.TaskViewModel
import kz.taskbar.app.ui.components.TaskEditorSheet
import kz.taskbar.app.ui.screens.HomeScreen
import kz.taskbar.app.ui.screens.TasksScreen
import kz.taskbar.app.ui.screens.InsightsScreen
import kz.taskbar.app.ui.screens.SettingsScreen
import kz.taskbar.app.ui.theme.TB
import kz.taskbar.app.ui.theme.TaskBarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TaskBarLog.i(TaskBarLog.APP, "MainActivity.onCreate")
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        setContent { TaskBarRoot() }
    }
}

private enum class Page(val title: String, val icon: ImageVector) {
    TODAY("Сегодня", Icons.Outlined.Home),
    TASKS("Задачи", Icons.Outlined.Checklist),
    INSIGHTS("Итоги", Icons.Outlined.BarChart),
    SETTINGS("Ещё", Icons.Outlined.Tune),
}

@Composable
private fun TaskBarRoot() {
    val application = LocalContext.current.applicationContext as TaskBarApplication
    val vm: TaskViewModel = viewModel(factory = remember(application) { TaskViewModel.Factory(application) })
    val tasks by vm.tasks.collectAsStateWithLifecycle()
    val dark by vm.isDark.collectAsStateWithLifecycle()
    val quote by vm.quote.collectAsStateWithLifecycle()
    var page by rememberSaveable { mutableStateOf(Page.TODAY) }
    var showEditor by rememberSaveable { mutableStateOf(false) }
    var editorTaskId by rememberSaveable { mutableLongStateOf(0L) }
    val editorTask = tasks.firstOrNull { it.id == editorTaskId }
    var editorSequence by rememberSaveable { mutableIntStateOf(0) }
    var pendingDelete by remember { mutableStateOf<TaskEntity?>(null) }
    val activity = LocalContext.current as? ComponentActivity

    SideEffect {
        activity?.let { act ->
            WindowCompat.getInsetsController(act.window, act.window.decorView)
                .isAppearanceLightStatusBars = !dark
            WindowCompat.getInsetsController(act.window, act.window.decorView)
                .isAppearanceLightNavigationBars = !dark
        }
    }
    val openNew = {
        editorTaskId = 0L
        editorSequence++
        showEditor = true
    }
    val openEdit: (TaskEntity) -> Unit = {
        editorTaskId = it.id
        editorSequence++
        showEditor = true
    }

    TaskBarTheme(darkTheme = dark) {
        Surface(Modifier.fillMaxSize().semantics { testTagsAsResourceId = true }, color = MaterialTheme.colorScheme.background) {
            Box(Modifier.fillMaxSize().statusBarsPadding()) {
                Box(Modifier.fillMaxSize().padding(bottom = 116.dp)) {
                    when (page) {
                        Page.TODAY -> HomeScreen(tasks, quote, onAll = { page = Page.TASKS },
                            onNew = openNew, onToggle = { vm.toggle(it) },
                            onEdit = openEdit, onDelete = { pendingDelete = it },
                            onQuoteRefresh = { vm.refreshQuote() })
                        Page.TASKS -> TasksScreen(tasks, onNew = openNew, onToggle = { vm.toggle(it) },
                            onEdit = openEdit, onDelete = { pendingDelete = it })
                        Page.INSIGHTS -> InsightsScreen(tasks)
                        Page.SETTINGS -> SettingsScreen(dark, tasks,
                            onDarkChange = { vm.setDark(it) }, onClearCompleted = { vm.clearCompleted() },
                            onOpenDebugLab = if (BuildConfig.DEBUG && activity != null) ({
                                activity.startActivity(Intent().setClassName(activity,
                                    "kz.taskbar.app.debug.DebugLabActivity"))
                            }) else null)
                    }
                }
                BottomDock(page, onSelect = { page = it },
                    modifier = Modifier.align(Alignment.BottomCenter))
                FloatingAdd(onClick = openNew,
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .navigationBarsPadding().padding(bottom = 67.dp))
            }
        }
        if (showEditor && (editorTaskId == 0L || editorTask != null)) {
            key(editorSequence) {
                TaskEditorSheet(task = editorTask, onDismiss = { showEditor = false },
                    onSave = { draft ->
                        vm.save(draft)
                        showEditor = false
                    })
            }
        }
        pendingDelete?.let { task ->
            AlertDialog(onDismissRequest = { pendingDelete = null },
                title = { Text("Удалить задачу?") },
                text = { Text("«${task.title}» будет удалена без возможности восстановления.") },
                confirmButton = { TextButton(onClick = {
                    vm.delete(task)
                    pendingDelete = null
                }) { Text("Удалить", color = MaterialTheme.colorScheme.error) } },
                dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Отмена") } })
        }
    }
}

@Composable
private fun BottomDock(selected: Page, onSelect: (Page) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().navigationBarsPadding()
        .padding(horizontal = 17.dp, vertical = 11.dp)
        .height(75.dp).clip(RoundedCornerShape(25.dp))
        .background(MaterialTheme.colorScheme.surface)
        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .6f), RoundedCornerShape(25.dp))
        .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        DockItem(Page.TODAY, selected == Page.TODAY, Modifier.weight(1f)) { onSelect(Page.TODAY) }
        DockItem(Page.TASKS, selected == Page.TASKS, Modifier.weight(1f)) { onSelect(Page.TASKS) }
        Spacer(Modifier.weight(.85f))
        DockItem(Page.INSIGHTS, selected == Page.INSIGHTS, Modifier.weight(1f)) { onSelect(Page.INSIGHTS) }
        DockItem(Page.SETTINGS, selected == Page.SETTINGS, Modifier.weight(1f)) { onSelect(Page.SETTINGS) }
    }
}

@Composable
private fun DockItem(page: Page, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.testTag("tab_${page.name.lowercase()}").clip(RoundedCornerShape(15.dp)).clickable(onClick = onClick)
        .padding(vertical = 9.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Icon(page.icon, contentDescription = page.title,
            tint = if (selected) TB.Lime else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(23.dp))
        Text(page.title, fontSize = 9.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) TB.Lime else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1)
    }
}

@Composable
private fun FloatingAdd(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.size(61.dp).testTag("add_task_button").clip(CircleShape).background(TB.Lime)
        .border(5.dp, MaterialTheme.colorScheme.background, CircleShape)
        .clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(Icons.Rounded.Add, "Добавить задачу", tint = TB.LimeInk,
            modifier = Modifier.size(31.dp))
    }
}
