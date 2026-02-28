package com.seraphim.delicacies.shared.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "check_in_records",
    indices = [Index(value = ["date", "meal_type"], unique = true)]
)
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "date")
    val date: String, // yyyy-MM-dd

    @ColumnInfo(name = "meal_type")
    val mealType: String, // LUNCH or DINNER

    @ColumnInfo(name = "checked_in")
    val checkedIn: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: String, // ISO-8601 datetime

    @ColumnInfo(name = "updated_at")
    val updatedAt: String, // ISO-8601 datetime
)
