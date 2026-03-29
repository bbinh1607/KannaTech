package loli.kanna

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class LockScreenReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        // CHỈ xử lý khi người dùng BẬT màn hình (ACTION_SCREEN_ON)
        // Điều này ngăn chặn việc Activity tự bật lên ngay khi vừa nhấn nút nguồn tắt máy.
        if (intent.action == Intent.ACTION_SCREEN_ON) {
            Log.d("LockScreenReceiver", "Screen turned ON. Opening LockScreenActivity...")

            val lockIntent =
                Intent(context, LockScreenActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                }

            // Mở trực tiếp Activity và KHÔNG tạo thêm bất kỳ Notification nào ở đây.
            try {
                context.startActivity(lockIntent)
            } catch (e: Exception) {
                Log.e("LockScreenReceiver", "Failed to start LockScreenActivity: ${e.message}")
            }
        }
    }
}
