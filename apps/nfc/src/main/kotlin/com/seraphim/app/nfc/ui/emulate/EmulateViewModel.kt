package com.seraphim.app.nfc.ui.emulate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seraphim.nfc.shared.domain.model.HillCardInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 模拟模式 ViewModel
 *
 * 状态管理：
 * - 当前激活的卡片
 * - 模拟激活状态
 * - APDU 交互日志
 */
class EmulateViewModel : ViewModel() {

    private val _activeCard = MutableStateFlow<HillCardInfo?>(null)
    val activeCard: StateFlow<HillCardInfo?> = _activeCard.asStateFlow()

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _statusLog = MutableStateFlow<List<String>>(emptyList())
    val statusLog: StateFlow<List<String>> = _statusLog.asStateFlow()

    /**
     * 设置要模拟的卡片
     */
    fun setCard(card: HillCardInfo) {
        _activeCard.value = card
        addLog("已选择卡片: ${card.name} (${card.uid})")
    }

    /**
     * 激活模拟（通知 HCE 服务）
     */
    fun activate() {
        val card = _activeCard.value ?: run {
            addLog("错误: 未选择卡片")
            return
        }
        _isActive.value = true
        addLog("模拟已激活: ${card.uid}")
        // TODO: 通过 LocalBroadcast / Service binding 通知 HillCardEmulationService
    }

    /**
     * 停用模拟
     */
    fun deactivate() {
        _isActive.value = false
        addLog("模拟已停止")
    }

    /**
     * 添加 APDU 交互日志（由 HCE Service 回调）
     */
    fun addApduLog(direction: String, data: String) {
        addLog("[$direction] $data")
    }

    private fun addLog(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault())
            .format(java.util.Date())
        _statusLog.value = _statusLog.value + "[$timestamp] $message"
    }

    override fun onCleared() {
        super.onCleared()
        deactivate()
    }
}
