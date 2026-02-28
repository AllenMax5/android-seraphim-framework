package com.seraphim.delicacies.shared.data.repository

import com.seraphim.delicacies.shared.domain.model.CheckInRecord
import com.seraphim.delicacies.shared.domain.model.DayCheckInStatus
import com.seraphim.delicacies.shared.domain.model.MealType
import com.seraphim.delicacies.shared.domain.model.MonthlyStats
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface CheckInRepository {
    fun getRecordsByMonth(year: Int, month: Int): Flow<List<CheckInRecord>>
    fun getRecordsByDate(date: LocalDate): Flow<List<CheckInRecord>>
    fun getDayCheckInStatusByMonth(year: Int, month: Int): Flow<Map<LocalDate, DayCheckInStatus>>
    fun getMonthlyStats(year: Int, month: Int): Flow<MonthlyStats>
    suspend fun checkIn(date: LocalDate, mealType: MealType)
    suspend fun updateCheckIn(date: LocalDate, mealType: MealType, checkedIn: Boolean)
    suspend fun deleteAll()
    suspend fun getAllCheckedInRecords(): List<CheckInRecord>
}
