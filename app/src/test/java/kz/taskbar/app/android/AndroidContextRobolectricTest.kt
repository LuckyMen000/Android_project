package kz.taskbar.app.android

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import kz.taskbar.app.MainActivity
import kz.taskbar.app.R
import kz.taskbar.app.data.LegacySettings
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** JVM + Robolectric: SharedPreferences, Android resources, and Intent without emulator. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AndroidContextRobolectricTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test fun sharedPreferencesRoundTrip() {
        val store = LegacySettings(context)
        store.writeDarkTheme(false)
        assertEquals(false, store.readDarkTheme())
        store.writeDarkTheme(true)
        assertEquals(true, store.readDarkTheme())
    }

    @Test fun androidResourcesAreReadable() {
        assertEquals("TaskBar", context.getString(R.string.app_name))
    }

    @Test fun explicitIntentTargetsMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        assertEquals(MainActivity::class.java.name, intent.component?.className)
    }
}
