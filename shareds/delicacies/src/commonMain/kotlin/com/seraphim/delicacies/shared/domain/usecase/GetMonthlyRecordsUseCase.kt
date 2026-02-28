package com.seraphim.delicacies.shared.domain.usecase

import com.seraphim.delicacies.shared.data.repository.CheckInRepository
import com.seraphim.delicacies.shared.domain.model.DayCheckInStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class GetMonthlyRecordsUseCase(
    private val repository: CheckInRepository,
) {
    operator fun invoke(year: Int, month: Int): Flow<Map<LocalDate, DayCheckInStatus>> {
        return repository.getDayCheckInStatusByMonth(year, month)
    }
}
