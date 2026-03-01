package com.seraphim.core.storage.db

import androidx.room.RoomDatabase

/**
 * 数据库工厂接口，用于创建平台相关的 RoomDatabase Builder。
 * 各平台（Android / iOS）需提供具体实现。
 *
 * 使用示例：
 * ```
 * class AndroidDatabaseFactory(private val context: Context) : DatabaseFactory {
 *     override fun <T : RoomDatabase> create(
 *         klass: kotlin.reflect.KClass<T>,
 *         name: String
 *     ): RoomDatabase.Builder<T> {
 *         val dbFile = context.getDatabasePath(name)
 *         return Room.databaseBuilder(context, klass, dbFile.absolutePath)
 *     }
 * }
 * ```
 */
interface DatabaseFactory {

    /**
     * 创建一个 RoomDatabase.Builder 实例。
     *
     * @param T 数据库类型，必须继承自 RoomDatabase
     * @param klass 数据库的 KClass
     * @param name 数据库文件名
     * @return RoomDatabase.Builder 实例
     */
    fun <T : RoomDatabase> create(
        klass: kotlin.reflect.KClass<T>,
        name: String,
    ): RoomDatabase.Builder<T>
}
