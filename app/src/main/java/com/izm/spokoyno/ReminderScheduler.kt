package com.izm.spokoyno

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val WORK_TAG = "SPOKOYNO_REMINDER_WORK"

    fun schedule(context: Context, enabled: Boolean, freq: String, time: String, day: Int) {
        val workManager = WorkManager.getInstance(context)

        // Сначала отменяем все старые задачи
        workManager.cancelAllWorkByTag(WORK_TAG)

        if (!enabled) return

        // Парсим время "HH:mm"
        val hour = time.substringBefore(":").toIntOrNull() ?: 10
        val minute = time.substringAfter(":").toIntOrNull() ?: 0

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (freq == "daily") {
            // Если время уже прошло сегодня, ставим на завтра
            if (target.before(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            val delay = target.timeInMillis - now.timeInMillis

            val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(WORK_TAG)
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        } else {
            // Еженедельно
            // day: 1 = Понедельник ... 7 = Воскресенье. Calendar: 1 = Sunday, 2 = Monday ...
            val calendarDay = if (day == 7) 1 else day + 1
            target.set(Calendar.DAY_OF_WEEK, calendarDay)

            if (target.before(now)) {
                target.add(Calendar.WEEK_OF_YEAR, 1)
            }
            val delay = target.timeInMillis - now.timeInMillis

            val request = PeriodicWorkRequestBuilder<ReminderWorker>(7, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(WORK_TAG)
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}