package kz.taskbar.app.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import kz.taskbar.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Intents instrumentation verifies TaskBar navigates to its debug-only Android Activity. */
@RunWith(AndroidJUnit4::class)
class TaskBarEspressoIntentsTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test fun openingDebugLabIssuesExpectedExplicitIntent() {
        Intents.init()
        try {
            TaskBarPage(rule).openSettings().openDebugLab()
            Intents.intended(hasComponent("kz.taskbar.app.debug.DebugLabActivity"))
        } finally {
            Intents.release()
        }
    }
}
