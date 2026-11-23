package kz.taskbar.app.ui

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.junit4.ComposeTestRule

/** Page Object: all UI selectors and user actions in one place. */
class TaskBarPage(private val rule: ComposeTestRule) {
    // TaskItem has a stable semantics tag task_<Room ID> on its clickable row.
    // Match that row rather than the TextField, which can contain the same title.
    private val taskCard = SemanticsMatcher("saved task card (task_<id>)") { node ->
        node.config.getOrNull(SemanticsProperties.TestTag)
            ?.matches(Regex("task_[0-9]+")) == true
    }

    fun openTasks() = apply { rule.onNodeWithTag("tab_tasks").performClick() }
    fun openInsights() = apply { rule.onNodeWithTag("tab_insights").performClick() }
    fun openSettings() = apply { rule.onNodeWithTag("tab_settings").performClick() }
    fun openToday() = apply { rule.onNodeWithTag("tab_today").performClick() }

    fun addTask(title: String) = apply {
        rule.onNodeWithTag("add_task_button").performClick()
        rule.onNodeWithTag("task_title_input").performTextInput(title)
        // Use the Compose IME action instead of Espresso's Activity-root keyboard helper:
        // the editor is a Dialog with its own focused window.
        rule.onNodeWithTag("task_title_input").performImeAction()
        rule.onNodeWithTag("task_save_button").performScrollTo().performClick()
        // A successful save dismisses the editor; fail here if validation prevents it.
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodesWithTag("task_save_button").fetchSemanticsNodes().isEmpty()
        }
    }

    fun search(query: String) = apply {
        rule.onNodeWithTag("task_search").performTextReplacement(query)
        // Hide the keyboard through the field's explicit Search action.
        rule.onNodeWithTag("task_search").performImeAction()
        rule.waitForIdle()
    }

    fun assertTaskVisible(title: String) = apply {
        // First confirm the filter found exactly one task. This observable UI
        // state is updated from Room, so a missing save cannot pass the test.
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodesWithText("Найдено: 1").fetchSemanticsNodes().isNotEmpty()
        }
        // LazyColumn only composes visible rows; scroll to the actual card.
        rule.onNodeWithTag("tasks_screen").performScrollToNode(taskCard)
        rule.onNode(taskCard).assertIsDisplayed()
        rule.onNodeWithTag("task_item_title", useUnmergedTree = true)
            .assertTextEquals(title)
            .assertIsDisplayed()
    }

    fun assertTasksPage() = apply { rule.onNodeWithTag("tasks_screen").assertExists() }
    fun openDebugLab() = apply {
        rule.onNodeWithTag("debug_lab_entry").performScrollTo().performClick()
    }
}
