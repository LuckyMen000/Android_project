package kz.taskbar.app.ui

import android.app.Activity
import android.app.Instrumentation

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
            val debugActivity = hasComponent("kz.taskbar.app.debug.DebugLabActivity")
            // Verify the actual explicit Intent without navigating away from
            // the Activity being exercised by the Compose test rule. Otherwise
            // Espresso's root loses window focus before intended() checks it.
            Intents.intending(debugActivity)
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null))
            TaskBarPage(rule).openSettings().openDebugLab()
            Intents.intended(debugActivity)
        } finally {
            Intents.release()
        }
    }
}
