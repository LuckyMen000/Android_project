package kz.taskbar.app.domain

import org.junit.Assert.*
import org.junit.Test

/** First baseline tests. RGR #3 adds a full unit/instrumented test suite later. */
class TaskValidatorTest {
    @Test fun blankTitleIsRejected() {
        assertNotNull(TaskValidator.validate("    ", ""))
    }

    @Test fun nonEmptyTitleIsAccepted() {
        assertNull(TaskValidator.validate("Подготовить отчёт", ""))
    }

    @Test fun maxLengthTitleIsAccepted() {
        assertNull(TaskValidator.validate("а".repeat(TaskValidator.MAX_TITLE), ""))
    }

    @Test fun tooLongTitleIsRejected() {
        assertNotNull(TaskValidator.validate("а".repeat(TaskValidator.MAX_TITLE + 1), ""))
    }

    @Test fun tooLongDescriptionIsRejected() {
        assertNotNull(TaskValidator.validate("Задача", "б".repeat(TaskValidator.MAX_DESCRIPTION + 1)))
    }

    @Test fun parsingUnknownCategoryFallsBackToPersonal() {
        assertEquals(TaskCategory.PERSONAL, TaskCategory.fromValue("NON_EXISTENT"))
    }

    @Test fun sameCalendarDayWorksWithDifferentTimes() {
        assertTrue(isSameDay(timeToday(8), timeToday(21)))
    }
}
