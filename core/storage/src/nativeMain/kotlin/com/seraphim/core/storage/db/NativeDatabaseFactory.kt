package com.seraphim.core.storage.db

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

/**
 * iOS/Native 平台的数据库工厂实现。
 * 使用 NSHomeDirectory 创建 RoomDatabase.Builder。
 */
class NativeDatabaseFactory : DatabaseFactory {

    override fun <T : RoomDatabase> create(
        klass: kotlin.reflect.KClass<T>,
        name: String,
    ): RoomDatabase.Builder<T> {
        val dbFilePath = "${NSHomeDirectory()}/Documents/$name"
        return Room.databaseBuilder(
            name = dbFilePath,
        )
    }
}
