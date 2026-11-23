package kz.taskbar.app.ui

import androidx.test.espresso.IdlingResource
import kz.taskbar.app.diagnostics.TaskOperationTracker

/** Espresso waits for TaskViewModel coroutines instead of Thread.sleep(). */
class TaskBarIdlingResource : IdlingResource {
    @Volatile private var callback: IdlingResource.ResourceCallback? = null
    private val listener: () -> Unit = { callback?.onTransitionToIdle() }

    init { TaskOperationTracker.listen(listener) }
    override fun getName() = "TaskBar background operations"
    override fun isIdleNow(): Boolean = TaskOperationTracker.activeCount == 0
    override fun registerIdleTransitionCallback(cb: IdlingResource.ResourceCallback) {
        callback = cb
        if (isIdleNow) cb.onTransitionToIdle()
    }
    fun close() { TaskOperationTracker.unlisten(listener); callback = null }
}
