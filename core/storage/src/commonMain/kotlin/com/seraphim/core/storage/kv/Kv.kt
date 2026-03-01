package com.seraphim.core.storage.kv

import com.ctrip.flight.mmkv.defaultMMKV

private val kv = defaultMMKV()

/**
 * 将当前值安全地保存到 MMKV 键值存储中。
 * 支持的类型：String、Int、Boolean、Float、Long。
 *
 * @param key 存储的键
 * @throws IllegalArgumentException 如果类型不被支持
 */
fun Any.safeKvSave(key: String) {
    when (this) {
        is String -> kv[key] = this
        is Int -> kv[key] = this
        is Boolean -> kv[key] = this
        is Float -> kv[key] = this
        is Long -> kv[key] = this
        else -> throw IllegalArgumentException("Unsupported type: ${this::class.simpleName}")
    }
}

/**
 * 从 MMKV 键值存储中安全地读取值。
 * 支持的类型：String、Int、Boolean、Float、Long。
 *
 * @param key 存储的键
 * @param defaultValue 如果键不存在时返回的默认值（同时用于类型推断）
 * @return 存储中的值，如果不存在则返回 defaultValue
 * @throws IllegalArgumentException 如果类型不被支持
 */
@Suppress("UNCHECKED_CAST")
fun <T> safeKvGet(key: String, defaultValue: T): T {
    return when (defaultValue) {
        is String -> kv.getString(key, defaultValue) as T
        is Int -> kv.getInt(key, defaultValue) as T
        is Boolean -> kv.getBoolean(key, defaultValue) as T
        is Float -> kv.getFloat(key, defaultValue) as T
        is Long -> kv.getLong(key, defaultValue) as T
        else -> throw IllegalArgumentException("Unsupported type: ${defaultValue?.let { it::class.simpleName }}")
    }
}

/**
 * 从 MMKV 键值存储中删除指定键。
 *
 * @param key 要删除的键
 */
fun kvRemove(key: String) {
    kv.removeValueForKey(key)
}

/**
 * 检查 MMKV 键值存储中是否包含指定键。
 *
 * @param key 要检查的键
 * @return 如果键存在则返回 true
 */
fun kvContains(key: String): Boolean {
    return kv.containsKey(key)
}

/**
 * 清除 MMKV 键值存储中的所有数据。
 */
fun kvClearAll() {
    kv.clearAll()
}
