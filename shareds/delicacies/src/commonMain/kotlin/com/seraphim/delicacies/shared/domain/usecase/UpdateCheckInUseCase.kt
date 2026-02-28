package com.seraphim.delicacies.shared.domain.usecase

import com.seraphim.delicacies.shared.data.repository.CheckInRepository
import com.seraphim.delicacies.shared.domain.model.MealType
import kotlinx.datetime.LocalDate

class UpdateCheckInUseCase(
    private val repository: CheckInRepository,
) {
    suspend operator fun invoke(
        date: LocalDate,
        mealType: MealType,
        checkedIn: Boolean,
    ): Result<Unit> {
        return try {
            repository.updateCheckIn(date, mealType, checkedIn)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
