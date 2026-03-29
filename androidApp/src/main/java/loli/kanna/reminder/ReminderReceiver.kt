package loli.kanna.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import loli.kanna.MainActivity
import loli.kanna.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getIntOfExtra("HABIT_ID", 0)
        val habitName = intent.getStringExtra("HABIT_NAME") ?: "Thói quen"
        val habitIcon = intent.getStringExtra("HABIT_ICON") ?: "⏰"

        showNotification(context, habitId, habitName, habitIcon)
    }

    private fun showNotification(context: Context, id: Int, name: String, icon: String) {
        val channelId = "habit_reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nhắc nhở thói quen",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$icon Đến giờ rồi!")
            .setContentText("Đã đến lúc thực hiện thói quen: $name")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id, notification)
    }
}

// Extension function to help getting int extra safely
fun Intent.getIntOfExtra(name: String, defaultValue: Int): Int {
    return if (hasExtra(name)) getIntExtra(name, defaultValue) else defaultValue
}
