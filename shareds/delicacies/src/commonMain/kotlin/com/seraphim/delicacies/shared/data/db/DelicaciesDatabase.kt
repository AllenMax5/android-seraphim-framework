package com.seraphim.delicacies.shared.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [CheckInEntity::class],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(DelicaciesDatabaseConstructor::class)
abstract class DelicaciesDatabase : RoomDatabase() {
    abstract fun checkInDao(): CheckInDao
}

// Auto-implemented by Room KSP
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object DelicaciesDatabaseConstructor : RoomDatabaseConstructor<DelicaciesDatabase>
