package com.seraphim.app.nfc.security

import android.content.Context
import android.widget.Toast

/**
 * 生物识别认证管理器（骨架）
 *
 * TODO: 网络恢复后引入 androidx.biometric 依赖并完整实现
 * 当前为骨架代码，保留接口结构。
 */
class BiometricAuthManager(private val context: Context) {

    /**
     * 检查设备是否支持生物识别
     */
    fun canAuthenticate(): Boolean {
        // TODO: 使用 BiometricManager 检测
        return false
    }

    /**
     * 获取生物识别状态描述
     */
    fun getStatus(): BiometricStatus {
        // TODO: 使用 BiometricManager 获取真实状态
        return BiometricStatus.NO_HARDWARE
    }

    /**
     * 触发生物识别认证
     */
    suspend fun authenticate(): BiometricResult {
        // TODO: 使用 BiometricPrompt 实现
        Toast.makeText(context, "生物识别功能待启用", Toast.LENGTH_SHORT).show()
        return BiometricResult.Error(-1, "依赖未加载")
    }
}

enum class BiometricStatus {
    AVAILABLE,
    NO_HARDWARE,
    HW_UNAVAILABLE,
    NOT_ENROLLED,
    UNKNOWN,
}

sealed class BiometricResult {
    data object Success : BiometricResult()
    data object Cancelled : BiometricResult()
    data object LockedOut : BiometricResult()
    data class Error(val code: Int, val message: String) : BiometricResult()
}
