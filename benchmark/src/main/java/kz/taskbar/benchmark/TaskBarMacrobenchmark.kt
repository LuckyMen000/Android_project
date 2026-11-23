package kz.taskbar.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Runs against the non-debuggable benchmark build; uses a separate Gradle module. */
@RunWith(AndroidJUnit4::class)
class TaskBarMacrobenchmark {
    @get:Rule val rule = MacrobenchmarkRule()
    private val targetPackage = "kz.taskbar.app"

    @Test fun coldStart() = rule.measureRepeated(
        packageName = targetPackage,
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.None(),
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test fun taskListScroll() = rule.measureRepeated(
        packageName = targetPackage,
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = null, // keep the app active after setup for scrolling measurement
        compilationMode = CompilationMode.None(),
        setupBlock = {
            pressHome()
            startActivityAndWait()
        },
    ) {
        val tasksTab = device.wait(Until.findObject(By.res("tab_tasks")), 5_000)
            ?: error("tab_tasks not found: check Compose testTagsAsResourceId")
        tasksTab.click()
        val list = device.wait(Until.findObject(By.res("tasks_screen")), 5_000)
            ?: error("tasks_screen not found")
        repeat(3) { list.scroll(Direction.DOWN, 0.7f) }
    }
}
