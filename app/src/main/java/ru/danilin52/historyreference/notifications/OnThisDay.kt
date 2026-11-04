package ru.danilin52.historyreference.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import ru.danilin52.historyreference.HistoryApp
import ru.danilin52.historyreference.R
import java.time.LocalDate
import java.util.concurrent.TimeUnit

private const val CHANNEL_ID = "on_this_day"
private const val WORK_NAME = "on_this_day_work"

class OnThisDayWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? HistoryApp ?: return Result.success()
        val today = LocalDate.now()
        val dayMonth = "%02d-%02d".format(today.monthValue, today.dayOfMonth)
        val events = app.repository.eventsOnDate(dayMonth)
        if (events.isEmpty()) return Result.success()

        val event = events.random()
        showNotification(applicationContext, event.title, event.description)
        return Result.success()
    }
}

object OnThisDayScheduler {

    fun schedule(context: Context) {
        createChannel(context)

        val request = PeriodicWorkRequestBuilder<OnThisDayWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.on_this_day_channel),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
    }
}

private fun showNotification(context: Context, title: String, text: String) {
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("В этот день: $title")
        .setContentText(text)
        .setStyle(NotificationCompat.BigTextStyle().bigText(text))
        .setAutoCancel(true)
        .build()

    runCatching {
        NotificationManagerCompat.from(context).notify(1, notification)
    }
}