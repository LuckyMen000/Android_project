package kz.taskbar.app.ui

import androidx.compose.ui.test.IdlingResource
import kz.taskbar.app.diagnostics.TaskOperationTracker

/** Compose test-rule adapter for the same asynchronous-operation counter. */
class TaskBarComposeIdle : IdlingResource {
    override val isIdleNow: Boolean
        get() = TaskOperationTracker.activeCount == 0
}
