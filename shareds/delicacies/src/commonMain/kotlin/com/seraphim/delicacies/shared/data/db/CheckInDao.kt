package com.seraphim.delicacies.shared.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {

    @Query("SELECT * FROM check_in_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getRecordsByDateRange(startDate: String, endDate: String): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM check_in_records WHERE date = :date")
    fun getRecordsByDate(date: String): Flow<List<CheckInEntity>>

    @Query("SELECT COUNT(*) FROM check_in_records WHERE date LIKE :monthPrefix || '%' AND checked_in = 1")
    fun getMonthlyCheckInCount(monthPrefix: String): Flow<Int>

    @Query(
        """
        SELECT meal_type, COUNT(*) as cnt 
        FROM check_in_records 
        WHERE date LIKE :monthPrefix || '%' AND checked_in = 1 
        GROUP BY meal_type
        """
    )
    fun getMonthlyCheckInCountByMealType(monthPrefix: String): Flow<List<MealTypeCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecord(record: CheckInEntity)

    @Query(
        """
        UPDATE check_in_records 
        SET checked_in = :status, updated_at = :updatedAt 
        WHERE date = :date AND meal_type = :mealType
        """
    )
    suspend fun updateCheckInStatus(
        date: String,
        mealType: String,
        status: Boolean,
        updatedAt: String,
    )

    @Query("DELETE FROM check_in_records WHERE date = :date AND meal_type = :mealType")
    suspend fun deleteRecord(date: String, mealType: String)

    @Query("DELETE FROM check_in_records")
    suspend fun deleteAll()

    @Query("SELECT * FROM check_in_records WHERE checked_in = 1 ORDER BY date ASC")
    suspend fun getAllCheckedInRecords(): List<CheckInEntity>
}

data class MealTypeCount(
    val meal_type: String,
    val cnt: Int,
)
