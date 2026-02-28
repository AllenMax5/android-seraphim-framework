package com.seraphim.delicacies.shared.domain.usecase

import com.seraphim.delicacies.shared.data.repository.CheckInRepository
import com.seraphim.delicacies.shared.domain.model.MealType
import com.seraphim.delicacies.shared.domain.model.MonthlyStats
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

class CheckInUseCase(
    private val repository: CheckInRepository,
) {
    suspend operator fun invoke(date: LocalDate, mealType: MealType): Result<Unit> {
        return try {
            // Check monthly limit
            val stats = repository.getMonthlyStats(date.year, date.monthNumber).first()
            if (stats.isLimitReached) {
                return Result.failure(MonthlyLimitReachedException(stats.maxCount))
            }

            // Check if already checked in for this meal
            val existing = repository.getRecordsByDate(date).first()
            if (existing.any { it.mealType == mealType && it.checkedIn }) {
                return Result.failure(AlreadyCheckedInException(mealType))
            }

            repository.checkIn(date, mealType)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class MonthlyLimitReachedException(val limit: Int) :
    Exception("本月签到已达上限 ($limit 次)")

class AlreadyCheckedInException(val mealType: MealType) :
    Exception("今日${if (mealType == MealType.LUNCH) "午餐" else "晚餐"}已签到")
