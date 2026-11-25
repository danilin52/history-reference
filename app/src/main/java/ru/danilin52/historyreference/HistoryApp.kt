package ru.danilin52.historyreference

import android.app.Application
import ru.danilin52.historyreference.data.AppDatabase
import ru.danilin52.historyreference.data.HistoryRepository
import ru.danilin52.historyreference.notifications.OnThisDayScheduler
import ru.danilin52.historyreference.preferences.UserPreferences

class HistoryApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.create(this) }
    val preferences: UserPreferences by lazy { UserPreferences(this) }
    val repository: HistoryRepository by lazy {
        HistoryRepository(database.historyDao(), preferences)
    }

    override fun onCreate() {
        super.onCreate()
        if (preferences.notificationsEnabled.value) {
            OnThisDayScheduler.schedule(this)
        }
    }
}
