package com.seraphim.app.yxsg.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.seraphim.app.yxsg.R
import com.seraphim.delicacies.shared.domain.model.MealType

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "delicacies_meal_reminder"
        const val CHANNEL_NAME = "用餐签到提醒"
        private const val LUNCH_NOTIFICATION_ID = 1001
        private const val DINNER_NOTIFICATION_ID = 1002
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "每日午餐和晚餐签到提醒"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showMealReminder(mealType: MealType) {
        val (title, text, id) = when (mealType) {
            MealType.LUNCH -> Triple(
                "午餐时间到了 🥗",
                "别忘了签到哦！记录你的午餐~",
                LUNCH_NOTIFICATION_ID,
            )
            MealType.DINNER -> Triple(
                "晚餐时间到了 🍜",
                "记得签到！今天也要好好吃饭~",
                DINNER_NOTIFICATION_ID,
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(id, notification)
    }
}
