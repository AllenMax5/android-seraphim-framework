package com.seraphim.app.yxsg.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seraphim.delicacies.shared.domain.model.MealType

class MealReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val mealTypeName = inputData.getString(KEY_MEAL_TYPE) ?: return Result.failure()
        val mealType = try {
            MealType.valueOf(mealTypeName)
        } catch (_: Exception) {
            return Result.failure()
        }
        val helper = NotificationHelper(applicationContext)
        helper.showMealReminder(mealType)
        return Result.success()
    }

    companion object {
        const val KEY_MEAL_TYPE = "meal_type"
    }
}
