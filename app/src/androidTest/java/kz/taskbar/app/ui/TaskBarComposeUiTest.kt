package kz.taskbar.app.ui

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kz.taskbar.app.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Compose counterpart of Espresso's onView/perform/check; Espresso IdlingResource is registered. */
@RunWith(AndroidJUnit4::class)
class TaskBarComposeUiTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()
    private lateinit var idle: TaskBarIdlingResource
    private val composeIdle = TaskBarComposeIdle()

    @Before fun setup() {
        idle = TaskBarIdlingResource()
        IdlingRegistry.getInstance().register(idle)
        rule.registerIdlingResource(composeIdle)
    }
    @After fun teardown() {
        rule.unregisterIdlingResource(composeIdle)
        IdlingRegistry.getInstance().unregister(idle)
        idle.close()
    }

    @Test fun createTaskSearchAndNavigate() {
        val unique = "RGR3 UI test task ${System.nanoTime()}"
        val page = TaskBarPage(rule)
        page.openTasks().assertTasksPage().addTask(unique).search(unique).assertTaskVisible(unique)
        page.openInsights()
        rule.onNodeWithTag("tab_insights").assertExists()
        page.openToday()
        rule.onNodeWithTag("tab_today").assertExists()
    }

    @Test fun unsavedDraftSurvivesActivityRecreation() {
        TaskBarPage(rule).openTasks()
        rule.onNodeWithTag("add_task_button").performClick()
        rule.onNodeWithTag("task_title_input").performTextInput("Несохранённый черновик")
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("task_title_input").assertExists().assertTextContains("Несохранённый черновик")
    }
}
