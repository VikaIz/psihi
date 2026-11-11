package com.izm.spokoyno

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Показываем уведомление
        NotificationHelper.showNotification(
            applicationContext,
            "Спокойно",
            "Пора пройти тест и проверить свое состояние! 😌"
        )
        return Result.success()
    }
}