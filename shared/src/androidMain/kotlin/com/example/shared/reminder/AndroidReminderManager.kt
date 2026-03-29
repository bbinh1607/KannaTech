package com.example.shared.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.shared.domain.model.Habit
import com.example.shared.domain.reminder.ReminderManager
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class AndroidReminderManager(
    private val context: Context,
) : ReminderManager {
    override fun scheduleReminder(habit: Habit) {
        // Ưu tiên dùng startTime, nếu không có mới dùng reminderTime
        val timeToSchedule = habit.startTime ?: habit.reminderTime
        
        Log.i("ReminderLog", ">>> [BƯỚC 1] BẮT ĐẦU ĐẶT LỊCH cho: ${habit.name} dựa trên GIỜ BẮT ĐẦU: $timeToSchedule")

        if (timeToSchedule == null) {
            Log.e("ReminderLog", ">>> LỖI: Cả startTime và reminderTime đều bị null")
            return
        }
        
        val parts = timeToSchedule.split(":")
        if (parts.size != 2) {
            Log.e("ReminderLog", ">>> LỖI: Định dạng giờ sai: $timeToSchedule")
            return
        }

        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        cancelReminder(habit.id)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("HABIT_ID", habit.id)
            putExtra("HABIT_NAME", habit.name)
            putExtra("HABIT_ICON", habit.icon)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habit.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val nowInstant = Clock.System.now()
        val now = nowInstant.toLocalDateTime(TimeZone.currentSystemDefault())
        
        val triggerTime = LocalDateTime(now.year, now.month, now.dayOfMonth, hour, minute, 0, 0)
        var finalTriggerInstant = triggerTime.toInstant(TimeZone.currentSystemDefault())

        if (finalTriggerInstant <= nowInstant) {
            finalTriggerInstant = finalTriggerInstant.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            Log.i("ReminderLog", ">>> Giờ đã qua, hẹn cho ngày mai: $finalTriggerInstant")
        }

        val triggerMillis = finalTriggerInstant.toEpochMilliseconds()
        Log.i("ReminderLog", ">>> [BƯỚC 2] THỜI ĐIỂM SẼ BÁO THỨC: $finalTriggerInstant (Millis: $triggerMillis)")

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                    Log.i("ReminderLog", ">>> [BƯỚC 3] ĐÃ GỌI setExact THÀNH CÔNG cho ${habit.name}")
                } else {
                    Log.e("ReminderLog", ">>> [BƯỚC 3] CẢNH BÁO: Thiếu quyền Exact Alarm!")
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                Log.i("ReminderLog", ">>> [BƯỚC 3] ĐÃ GỌI setExact THÀNH CÔNG (Android cũ)")
            }
        } catch (e: Exception) {
            Log.e("ReminderLog", ">>> LỖI HỆ THỐNG: ${e.message}")
        }
    }

    override fun cancelReminder(habitId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 
            habitId, 
            intent, 
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
