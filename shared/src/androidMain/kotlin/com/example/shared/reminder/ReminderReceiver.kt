package com.example.shared.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val habitId = intent.getIntOfExtra("HABIT_ID", 0)
        val habitName = intent.getStringExtra("HABIT_NAME") ?: "Thói quen"
        val habitIcon = intent.getStringExtra("HABIT_ICON") ?: "⏰"

        // LOG 1: Nhận được tín hiệu từ báo thức
        Log.d("ReminderLog", ">>> 1. ĐÃ NHẬN TÍN HIỆU BÁO THỨC cho thói quen: $habitName (ID: $habitId)")
        
        showNotification(context, habitId, habitName, habitIcon)
    }

    private fun showNotification(
        context: Context,
        id: Int,
        name: String,
        icon: String,
    ) {
        val channelId = "habit_reminder_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // LOG 2: Kiểm tra quyền thông báo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
            Log.d("ReminderLog", ">>> 2. KIỂM TRA QUYỀN THÔNG BÁO: ${if (hasPermission) "ĐÃ CÓ QUYỀN" else "BỊ CHẶN QUYỀN"}")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    channelId,
                    "Nhắc nhở thói quen",
                    NotificationManager.IMPORTANCE_HIGH,
                )
            notificationManager.createNotificationChannel(channel)
            Log.d("ReminderLog", ">>> 3. ĐÃ TẠO/CẬP NHẬT CHANNEL THÔNG BÁO")
        }

        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent =
            if (launchIntent != null) {
                PendingIntent.getActivity(
                    context,
                    id,
                    launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            } else {
                null
            }

        val notificationBuilder =
            NotificationCompat
                .Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("$icon $name")
                .setContentText("Đã đến lúc thực hiện thói quen: $name")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

        pendingIntent?.let {
            notificationBuilder.setContentIntent(it)
        }

        // LOG 4: Lệnh cuối cùng gửi thông báo lên Android
        try {
            notificationManager.notify(id, notificationBuilder.build())
            Log.d("ReminderLog", ">>> 4. ĐÃ GỬI LỆNH NOTIFY THÀNH CÔNG lên hệ thống (ID: $id)")
        } catch (e: Exception) {
            Log.e("ReminderLog", ">>> LỖI KHI GỬI THÔNG BÁO: ${e.message}")
        }
    }
}

fun Intent.getIntOfExtra(
    name: String,
    defaultValue: Int,
): Int = if (hasExtra(name)) getIntExtra(name, defaultValue) else defaultValue
