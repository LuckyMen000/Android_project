package kz.taskbar.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressCalculatorTest {
    @Test fun zeroTasks() = assertEquals(0, ProgressCalculator.completionPercent(0, 0))
    @Test fun partialProgress() = assertEquals(33, ProgressCalculator.completionPercent(2, 6))
    @Test fun complete() = assertEquals(100, ProgressCalculator.completionPercent(6, 6))
    @Test fun clampsNegative() = assertEquals(0, ProgressCalculator.completionPercent(-3, 6))
    @Test fun clampsOverTotal() = assertEquals(100, ProgressCalculator.completionPercent(9, 6))
    @Test fun zeroTotalEvenWithCompletion() = assertEquals(0, ProgressCalculator.completionPercent(2, 0))
}
