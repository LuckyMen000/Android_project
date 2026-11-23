package kz.taskbar.app.diagnostics

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

/** Tracks coroutine operations; a real Espresso IdlingResource wraps it in androidTest. */
object TaskOperationTracker {
    private val count = AtomicInteger(0)
    private val idleListeners = CopyOnWriteArrayList<() -> Unit>()

    val activeCount: Int get() = count.get()
    fun begin() { count.incrementAndGet() }
    fun end() {
        val left = count.decrementAndGet()
        check(left >= 0) { "Unbalanced tracked operation" }
        if (left == 0) idleListeners.forEach { it.invoke() }
    }
    fun listen(listener: () -> Unit) { idleListeners.add(listener) }
    fun unlisten(listener: () -> Unit) { idleListeners.remove(listener) }
}
