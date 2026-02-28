package com.seraphim.app.yxsg.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.seraphim.delicacies.shared.domain.model.MealType
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    private const val LUNCH_WORK_NAME = "delicacies_lunch_reminder"
    private const val DINNER_WORK_NAME = "delicacies_dinner_reminder"

    fun schedule(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // Lunch reminder — daily at 11:30
        val lunchRequest = PeriodicWorkRequestBuilder<MealReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(calculateDelay(11, 30), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(MealReminderWorker.KEY_MEAL_TYPE to MealType.LUNCH.name))
            .build()

        // Dinner reminder — daily at 17:30
        val dinnerRequest = PeriodicWorkRequestBuilder<MealReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(calculateDelay(17, 30), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(MealReminderWorker.KEY_MEAL_TYPE to MealType.DINNER.name))
            .build()

        workManager.enqueueUniquePeriodicWork(
            LUNCH_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            lunchRequest,
        )
        workManager.enqueueUniquePeriodicWork(
            DINNER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dinnerRequest,
        )
    }

    fun cancel(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(LUNCH_WORK_NAME)
        workManager.cancelUniqueWork(DINNER_WORK_NAME)
    }

    private fun calculateDelay(targetHour: Int, targetMinute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }
}
