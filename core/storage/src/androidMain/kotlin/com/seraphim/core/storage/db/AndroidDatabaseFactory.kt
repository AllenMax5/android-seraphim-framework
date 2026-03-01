package com.seraphim.core.storage.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Android 平台的数据库工厂实现。
 * 使用 Android Context 创建 RoomDatabase.Builder。
 *
 * @param context Android Context，用于获取数据库文件路径
 */
class AndroidDatabaseFactory(private val context: Context) : DatabaseFactory {

    override fun <T : RoomDatabase> create(
        klass: kotlin.reflect.KClass<T>,
        name: String,
    ): RoomDatabase.Builder<T> {
        val dbFile = context.getDatabasePath(name)
        return Room.databaseBuilder(
            context = context,
            klass = klass.java,
            name = dbFile.absolutePath,
        )
    }
}
