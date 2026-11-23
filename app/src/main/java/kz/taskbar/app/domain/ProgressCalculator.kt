package kz.taskbar.app.domain

import kotlin.math.roundToInt

/** Pure business logic with clamped inputs; suitable for JUnit and debugger. */
object ProgressCalculator {
    fun completionPercent(completed: Int, total: Int): Int {
        if (total <= 0) return 0
        return (completed.coerceIn(0, total).toDouble() / total * 100).roundToInt()
    }
}
