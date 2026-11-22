package kz.taskbar.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.taskbarDataStore by preferencesDataStore(name = "taskbar_settings")

class PreferencesRepository(private val context: Context) {
    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val seededKey = booleanPreferencesKey("demo_seeded")

    val isDarkTheme: Flow<Boolean> = context.taskbarDataStore.data.map { it[darkThemeKey] ?: true }
    suspend fun setDarkTheme(enabled: Boolean) {
        context.taskbarDataStore.edit { it[darkThemeKey] = enabled }
    }
    suspend fun hasSeeded(): Boolean = context.taskbarDataStore.data.first()[seededKey] ?: false
    suspend fun markSeeded() { context.taskbarDataStore.edit { it[seededKey] = true } }
}
