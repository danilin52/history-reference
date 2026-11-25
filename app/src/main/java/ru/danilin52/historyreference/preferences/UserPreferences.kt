package ru.danilin52.historyreference.preferences

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        FILE_NAME,
        Context.MODE_PRIVATE
    )

    private val _userName = MutableStateFlow(preferences.getString(KEY_USER_NAME, "Историк").orEmpty())
    val userName = _userName.asStateFlow()

    private val _darkTheme = MutableStateFlow(preferences.getBoolean(KEY_DARK_THEME, false))
    val darkTheme = _darkTheme.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(
        preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)
    )
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    fun setUserName(value: String) {
        preferences.edit().putString(KEY_USER_NAME, value).apply()
        _userName.value = value
    }

    fun setDarkTheme(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
        _darkTheme.value = enabled
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
        _notificationsEnabled.value = enabled
    }

    internal var databaseSeeded: Boolean
        get() = preferences.getBoolean(KEY_DATABASE_SEEDED, false)
        set(value) {
            preferences.edit().putBoolean(KEY_DATABASE_SEEDED, value).apply()
        }

    private companion object {
        const val FILE_NAME = "user_settings"
        const val KEY_USER_NAME = "user_name"
        const val KEY_DARK_THEME = "dark_theme"
        const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val KEY_DATABASE_SEEDED = "database_seeded"
    }
}
