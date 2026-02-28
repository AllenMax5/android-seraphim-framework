package com.seraphim.delicacies.shared.domain.usecase

import com.seraphim.delicacies.shared.data.repository.CheckInRepository
import com.seraphim.delicacies.shared.domain.model.MonthlyStats
import kotlinx.coroutines.flow.Flow

class GetMonthlyStatsUseCase(
    private val repository: CheckInRepository,
) {
    operator fun invoke(year: Int, month: Int): Flow<MonthlyStats> {
        return repository.getMonthlyStats(year, month)
    }
}
