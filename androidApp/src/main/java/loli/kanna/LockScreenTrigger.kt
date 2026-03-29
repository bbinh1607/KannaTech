package loli.kanna

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object LockScreenTrigger {

    fun triggerAfter5s(context: Context) {
        val intent = Intent(context, LockScreenActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 5000, // 5 giây
            pendingIntent
        )
    }
}