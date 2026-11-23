package kz.taskbar.app.ui

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.junit4.ComposeTestRule

/** Page Object: all UI selectors and user actions in one place. */
class TaskBarPage(private val rule: ComposeTestRule) {
    fun openTasks() = apply { rule.onNodeWithTag("tab_tasks").performClick() }
    fun openInsights() = apply { rule.onNodeWithTag("tab_insights").performClick() }
    fun openSettings() = apply { rule.onNodeWithTag("tab_settings").performClick() }
    fun openToday() = apply { rule.onNodeWithTag("tab_today").performClick() }

    fun addTask(title: String) = apply {
        rule.onNodeWithTag("add_task_button").performClick()
        rule.onNodeWithTag("task_title_input").performTextInput(title)
        rule.onNodeWithTag("task_save_button").performClick()
    }

    fun search(query: String) = apply {
        rule.onNodeWithTag("task_search").performTextReplacement(query)
    }

    fun assertTaskVisible(title: String) = apply {
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText(title).assertIsDisplayed()
    }

    fun assertTasksPage() = apply { rule.onNodeWithTag("tasks_screen").assertExists() }
    fun openDebugLab() = apply {
        rule.onNodeWithTag("debug_lab_entry").performScrollTo().performClick()
    }
}
