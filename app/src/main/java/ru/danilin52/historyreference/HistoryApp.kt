package ru.danilin52.historyreference

import android.app.Application
import ru.danilin52.historyreference.data.AppDatabase
import ru.danilin52.historyreference.data.HistoryRepository
import ru.danilin52.historyreference.notifications.OnThisDayScheduler

class HistoryApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.create(this) }
    val repository: HistoryRepository by lazy { HistoryRepository(database.historyDao()) }

    override fun onCreate() {
        super.onCreate()
        OnThisDayScheduler.schedule(this)
    }
}