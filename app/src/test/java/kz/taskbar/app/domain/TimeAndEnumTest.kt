package kz.taskbar.app.domain

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class TimeAndEnumTest {
    @Test fun shiftDayRoundTrip() {
        val reference = timeToday(12)
        assertTrue(shiftDays(reference, 1) > reference)
        assertEquals(startOfDay(reference), startOfDay(shiftDays(shiftDays(reference, 1), -1)))
    }

    @Test fun startOfDayIsBeforeMidday() {
        val time = timeToday(15, 30)
        assertTrue(startOfDay(time) <= time)
        assertTrue(time < shiftDays(startOfDay(time), 1))
    }

    @Test fun categoriesParseKnownAndUnknown() {
        assertEquals(TaskCategory.STUDY, TaskCategory.fromValue("STUDY"))
        assertEquals(TaskCategory.PERSONAL, TaskCategory.fromValue("invalid"))
    }

    @Test fun priorityUsesSafeDefault() {
        assertEquals(TaskPriority.HIGH, TaskPriority.fromValue("HIGH"))
        assertEquals(TaskPriority.MEDIUM, TaskPriority.fromValue("INVALID"))
    }
}
