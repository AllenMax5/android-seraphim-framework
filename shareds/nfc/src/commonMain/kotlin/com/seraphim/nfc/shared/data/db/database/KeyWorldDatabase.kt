package com.seraphim.nfc.shared.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.ConstructedBy
import androidx.room.RoomDatabaseConstructor
import com.seraphim.nfc.shared.data.db.dao.CardGroupDao
import com.seraphim.nfc.shared.data.db.dao.HillCardDao
import com.seraphim.nfc.shared.data.db.entity.CardGroupEntity
import com.seraphim.nfc.shared.data.db.entity.HillCardEntity
import kotlinx.datetime.Instant

@Database(
    entities = [HillCardEntity::class, CardGroupEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
@ConstructedBy(KeyWorldDatabaseConstructor::class)
abstract class KeyWorldDatabase : RoomDatabase() {
    abstract fun hillCardDao(): HillCardDao
    abstract fun cardGroupDao(): CardGroupDao
}

// Room KMP compiler generates the actual implementation automatically.
expect object KeyWorldDatabaseConstructor : RoomDatabaseConstructor<KeyWorldDatabase> {
    override fun initialize(): KeyWorldDatabase
}

class Converters {
    @TypeConverter
    fun fromInstant(instant: Instant): String = instant.toString()

    @TypeConverter
    fun toInstant(value: String): Instant = Instant.parse(value)
}
