package kz.taskbar.app.data

import android.content.Context

/** Isolated legacy SharedPreferences reader for Android-context / Robolectric tests.
 * Current user preferences are stored in DataStore, not this legacy file.
 */
class LegacySettings(context: Context) {
    private val prefs = context.getSharedPreferences("taskbar_legacy_settings", Context.MODE_PRIVATE)

    fun readDarkTheme(): Boolean? =
        if (prefs.contains("dark_theme")) prefs.getBoolean("dark_theme", true) else null

    fun writeDarkTheme(enabled: Boolean) {
        prefs.edit().putBoolean("dark_theme", enabled).apply()
    }
}
