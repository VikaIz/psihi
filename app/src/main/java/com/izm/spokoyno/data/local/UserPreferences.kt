package com.izm.spokoyno.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {
    // Уведомления
    private val REMINDERS_KEY = booleanPreferencesKey("reminders_enabled")
    private val REMINDER_FREQ_KEY = stringPreferencesKey("reminder_freq") // "daily" или "weekly"
    private val REMINDER_TIME_KEY = stringPreferencesKey("reminder_time") // "HH:mm"
    private val REMINDER_DAY_KEY = intPreferencesKey("reminder_day") // 1 = Пн, 7 = Вс

    // Тема (0 = системная, 1 = светлая, 2 = темная)
    private val THEME_KEY = intPreferencesKey("app_theme")

    // --- ТЕМА ---
    val theme = context.dataStore.data.map { prefs ->
        prefs[THEME_KEY] ?: 0
    }
    suspend fun setTheme(theme: Int) {
        context.dataStore.edit { prefs -> prefs[THEME_KEY] = theme }
    }

    // --- УВЕДОМЛЕНИЯ ---
    val remindersEnabled = context.dataStore.data.map { prefs ->
        prefs[REMINDERS_KEY] ?: false
    }
    suspend fun setRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[REMINDERS_KEY] = enabled }
    }

    val reminderFrequency = context.dataStore.data.map { prefs ->
        prefs[REMINDER_FREQ_KEY] ?: "weekly"
    }
    suspend fun setReminderFrequency(freq: String) {
        context.dataStore.edit { prefs -> prefs[REMINDER_FREQ_KEY] = freq }
    }

    val reminderTime = context.dataStore.data.map { prefs ->
        prefs[REMINDER_TIME_KEY] ?: "10:00"
    }
    suspend fun setReminderTime(time: String) {
        context.dataStore.edit { prefs -> prefs[REMINDER_TIME_KEY] = time }
    }

    val reminderDay = context.dataStore.data.map { prefs ->
        prefs[REMINDER_DAY_KEY] ?: 1 // По умолчанию Понедельник
    }
    suspend fun setReminderDay(day: Int) {
        context.dataStore.edit { prefs -> prefs[REMINDER_DAY_KEY] = day }
    }
}