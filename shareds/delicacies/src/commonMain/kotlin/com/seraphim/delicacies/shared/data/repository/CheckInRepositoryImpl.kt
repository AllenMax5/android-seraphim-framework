package com.seraphim.delicacies.shared.data.repository

import com.seraphim.delicacies.shared.data.db.CheckInDao
import com.seraphim.delicacies.shared.data.db.CheckInEntity
import com.seraphim.delicacies.shared.data.db.toDomainModel
import com.seraphim.delicacies.shared.domain.model.CheckInRecord
import com.seraphim.delicacies.shared.domain.model.DayCheckInStatus
import com.seraphim.delicacies.shared.domain.model.MealType
import com.seraphim.delicacies.shared.domain.model.MonthlyStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CheckInRepositoryImpl(
    private val checkInDao: CheckInDao,
) : CheckInRepository {

    override fun getRecordsByMonth(year: Int, month: Int): Flow<List<CheckInRecord>> {
        val startDate = LocalDate(year, month, 1).toString()
        val endDate = getLastDayOfMonth(year, month).toString()
        return checkInDao.getRecordsByDateRange(startDate, endDate)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override fun getRecordsByDate(date: LocalDate): Flow<List<CheckInRecord>> {
        return checkInDao.getRecordsByDate(date.toString())
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override fun getDayCheckInStatusByMonth(
        year: Int,
        month: Int,
    ): Flow<Map<LocalDate, DayCheckInStatus>> {
        val startDate = LocalDate(year, month, 1).toString()
        val endDate = getLastDayOfMonth(year, month).toString()
        return checkInDao.getRecordsByDateRange(startDate, endDate)
            .map { entities ->
                entities
                    .filter { it.checkedIn }
                    .groupBy { LocalDate.parse(it.date) }
                    .mapValues { (date, records) ->
                        DayCheckInStatus(
                            date = date,
                            lunchCheckedIn = records.any { it.mealType == MealType.LUNCH.name },
                            dinnerCheckedIn = records.any { it.mealType == MealType.DINNER.name },
                        )
                    }
            }
    }

    override fun getMonthlyStats(year: Int, month: Int): Flow<MonthlyStats> {
        val monthPrefix = "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}"
        return combine(
            checkInDao.getMonthlyCheckInCount(monthPrefix),
            checkInDao.getMonthlyCheckInCountByMealType(monthPrefix),
        ) { total, byType ->
            val lunchCount = byType.find { it.meal_type == MealType.LUNCH.name }?.cnt ?: 0
            val dinnerCount = byType.find { it.meal_type == MealType.DINNER.name }?.cnt ?: 0
            MonthlyStats(
                totalCount = total,
                lunchCount = lunchCount,
                dinnerCount = dinnerCount,
            )
        }
    }

    override suspend fun checkIn(date: LocalDate, mealType: MealType) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val entity = CheckInEntity(
            date = date.toString(),
            mealType = mealType.name,
            checkedIn = true,
            createdAt = now.toString(),
            updatedAt = now.toString(),
        )
        checkInDao.upsertRecord(entity)
    }

    override suspend fun updateCheckIn(
        date: LocalDate,
        mealType: MealType,
        checkedIn: Boolean,
    ) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        if (checkedIn) {
            // Upsert a new record
            val entity = CheckInEntity(
                date = date.toString(),
                mealType = mealType.name,
                checkedIn = true,
                createdAt = now.toString(),
                updatedAt = now.toString(),
            )
            checkInDao.upsertRecord(entity)
        } else {
            // Delete the record
            checkInDao.deleteRecord(date.toString(), mealType.name)
        }
    }

    override suspend fun deleteAll() {
        checkInDao.deleteAll()
    }

    override suspend fun getAllCheckedInRecords(): List<CheckInRecord> {
        return checkInDao.getAllCheckedInRecords().map { it.toDomainModel() }
    }

    private fun getLastDayOfMonth(year: Int, month: Int): LocalDate {
        return if (month == 12) {
            LocalDate(year + 1, 1, 1)
        } else {
            LocalDate(year, month + 1, 1)
        }.let {
            LocalDate(it.year, it.monthNumber, it.dayOfMonth)
                .let { firstOfNext ->
                    LocalDate.fromEpochDays(firstOfNext.toEpochDays() - 1)
                }
        }
    }
}
