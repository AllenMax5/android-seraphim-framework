package com.seraphim.core.storage.db

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * 数据库构建辅助工具，统一配置 RoomDatabase。
 *
 * 使用示例：
 * ```
 * val database = DatabaseBuilder.build(
 *     factory = androidDatabaseFactory,
 *     klass = AppDatabase::class,
 *     name = "app.db"
 * )
 * ```
 */
object DatabaseBuilder {

    /**
     * 使用 DatabaseFactory 构建一个 RoomDatabase 实例。
     * 默认使用 BundledSQLiteDriver 和 IO Dispatcher。
     *
     * @param T 数据库类型
     * @param factory 平台特定的数据库工厂
     * @param klass 数据库的 KClass
     * @param name 数据库文件名
     * @param configure 可选的额外配置 lambda
     * @return 构建好的数据库实例
     */
    fun <T : RoomDatabase> build(
        factory: DatabaseFactory,
        klass: kotlin.reflect.KClass<T>,
        name: String,
        configure: (RoomDatabase.Builder<T>.() -> RoomDatabase.Builder<T>)? = null,
    ): T {
        return factory.create(klass, name)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .let { builder -> configure?.invoke(builder) ?: builder }
            .build()
    }
}
