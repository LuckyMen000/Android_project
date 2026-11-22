package kz.taskbar.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kz.taskbar.app.data.DemoTasks
import kz.taskbar.app.data.DailyQuote
import kz.taskbar.app.ui.screens.HomeScreen
import kz.taskbar.app.ui.screens.InsightsScreen
import kz.taskbar.app.ui.theme.TaskBarTheme

/** Open in Android Studio > Split > Preview to inspect the real Compose UI without running an app. */
@Preview(name = "Home / dark", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun HomeDarkPreview() {
    TaskBarTheme(darkTheme = true) {
        HomeScreen(DemoTasks.create(), DailyQuote("Главное — начать.", "TaskBar", false),
            onAll = {}, onNew = {}, onToggle = {}, onEdit = {}, onDelete = {}, onQuoteRefresh = {})
    }
}

@Preview(name = "Home / light", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun HomeLightPreview() {
    TaskBarTheme(darkTheme = false) {
        HomeScreen(DemoTasks.create(), DailyQuote("Главное — начать.", "TaskBar", false),
            onAll = {}, onNew = {}, onToggle = {}, onEdit = {}, onDelete = {}, onQuoteRefresh = {})
    }
}

@Preview(name = "Insights / dark", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun InsightsPreview() {
    TaskBarTheme(darkTheme = true) {
        InsightsScreen(DemoTasks.create())
    }
}
