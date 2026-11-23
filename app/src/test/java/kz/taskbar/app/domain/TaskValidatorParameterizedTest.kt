package kz.taskbar.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** Arrange - Act - Assert, including exact boundary lengths. */
@RunWith(Parameterized::class)
class TaskValidatorParameterizedTest(
    private val title: String,
    private val description: String,
    private val valid: Boolean,
) {
    companion object {
        @JvmStatic @Parameterized.Parameters(name = "{index}: expected valid={2}")
        fun cases(): Collection<Array<Any>> = listOf(
            arrayOf("", "", false),
            arrayOf("  ", "", false),
            arrayOf(" x ", "", true),
            arrayOf("X".repeat(90), "", true),
            arrayOf("X".repeat(91), "", false),
            arrayOf("valid", "y".repeat(1000), true),
            arrayOf("valid", "y".repeat(1001), false),
            arrayOf("Презентация", "Детали", true),
        )
    }

    @Test fun validateBoundary() {
        // Arrange: supplied parameter row
        // Act
        val error = TaskValidator.validate(title, description)
        // Assert
        assertEquals(valid, error == null)
    }
}
