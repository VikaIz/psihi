package com.izm.spokoyno

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    private const val CHANNEL_ID = "spokoyno_reminders"
    private const val CHANNEL_NAME = "Напоминания о тестах"

    fun createNotificationChannel(context: Context) {
        // Проверка SDK_INT >= O убрана, так как minSdk = 29
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Еженедельные напоминания пройти психологический тест"
            enableVibration(true)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun showNotification(context: Context, title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }
        NotificationManagerCompat.from(context).notify(1, builder.build())
    }
}