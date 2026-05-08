package com.seraphim.app.nfc.service

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import com.seraphim.nfc.shared.domain.model.HillCardInfo

/**
 * Hill 门禁卡 HCE 模拟服务
 *
 * 负责：
 * - 接收读卡器 APDU 命令
 * - 根据激活的卡片数据响应认证请求
 * - 处理 SELECT、AUTH、READ 等标准 MIFARE 指令
 *
 * APDU 格式：CLA | INS | P1 | P2 | Lc | Data | Le
 */
class HillCardEmulationService : HostApduService() {

    companion object {
        // 标准响应码
        val SW_SUCCESS = byteArrayOf(0x90.toByte(), 0x00.toByte())
        val SW_INS_NOT_SUPPORTED = byteArrayOf(0x6D.toByte(), 0x00.toByte())
        val SW_CLA_NOT_SUPPORTED = byteArrayOf(0x6E.toByte(), 0x00.toByte())
        val SW_WRONG_LENGTH = byteArrayOf(0x67.toByte(), 0x00.toByte())
        val SW_AUTH_FAILED = byteArrayOf(0x63.toByte(), 0x00.toByte())

        // APDU 指令
        const val INS_SELECT = 0xA4.toByte()
        const val INS_AUTH = 0x60.toByte()
        const val INS_READ = 0x30.toByte()
        const val INS_WRITE = 0x40.toByte()

        // AID（应用标识符）- Hill 门禁系统专用
        private val HILL_AID = byteArrayOf(
            0xF0.toByte(), 0x39.toByte(), 0x41.toByte(), 0x48,
            0x14.toByte(), 0x81.toByte(), 0x00.toByte()
        )
    }

    // 当前激活的卡片
    private var activeCard: HillCardInfo? = null

    // 认证状态
    private var authenticatedSectors = mutableSetOf<Int>()

    override fun onCreate() {
        super.onCreate()
        // TODO: 从本地存储加载默认卡片
    }

    /**
     * 处理读卡器发送的 APDU 命令
     */
    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        if (commandApdu.size < 4) {
            return SW_WRONG_LENGTH
        }

        val cla = commandApdu[0]
        val ins = commandApdu[1]
        val p1 = commandApdu[2]
        val p2 = commandApdu[3]

        // 检查 CLA
        if (cla != 0x00.toByte()) {
            return SW_CLA_NOT_SUPPORTED
        }

        val card = activeCard ?: return SW_INS_NOT_SUPPORTED

        return when (ins) {
            INS_SELECT -> handleSelect(commandApdu, p1, p2)
            INS_AUTH -> handleAuth(commandApdu, p1, p2, card)
            INS_READ -> handleRead(commandApdu, p1, p2, card)
            else -> SW_INS_NOT_SUPPORTED
        }
    }

    /**
     * 处理 SELECT 命令（选择应用/文件）
     */
    private fun handleSelect(apdu: ByteArray, p1: Byte, p2: Byte): ByteArray {
        // 验证 AID 匹配
        if (p1 == 0x04.toByte()) { // 通过名称选择
            val aidLength = apdu[4].toInt()
            val aid = apdu.copyOfRange(5, 5 + aidLength)
            if (aid.contentEquals(HILL_AID)) {
                return SW_SUCCESS
            }
        }
        return SW_SUCCESS // 简化处理，直接成功
    }

    /**
     * 处理 AUTH 认证命令
     */
    private fun handleAuth(apdu: ByteArray, p1: Byte, p2: Byte, card: HillCardInfo): ByteArray {
        val sectorIndex = p1.toInt()
        val keyType = p2.toInt() // 0 = KeyA, 1 = KeyB

        val sector = card.sectors.getOrNull(sectorIndex) ?: return SW_AUTH_FAILED

        // 验证密钥（简化：使用默认密钥）
        val key = if (keyType == 0) sector.keyA else sector.keyB
        if (key != null) {
            authenticatedSectors.add(sectorIndex)
            return SW_SUCCESS
        }

        return SW_AUTH_FAILED
    }

    /**
     * 处理 READ 读取命令
     */
    private fun handleRead(apdu: ByteArray, p1: Byte, p2: Byte, card: HillCardInfo): ByteArray {
        val blockIndex = p1.toInt()
        val sectorIndex = blockIndex / 4

        // 检查是否已认证（尾块不需要认证）
        if (!authenticatedSectors.contains(sectorIndex) && (blockIndex % 4 != 3)) {
            return SW_AUTH_FAILED
        }

        val sector = card.sectors.getOrNull(sectorIndex) ?: return SW_INS_NOT_SUPPORTED
        val block = sector.blocks.getOrNull(blockIndex % 4)
            ?: return SW_INS_NOT_SUPPORTED

        // 将 HEX 字符串转换为字节数组返回
        val data = block.data.split(":").map { it.toInt(16).toByte() }.toByteArray()

        return data + SW_SUCCESS
    }

    /**
     * 模拟被停用（手机离开读卡器、超时、切换应用）
     */
    override fun onDeactivated(reason: Int) {
        authenticatedSectors.clear()
        when (reason) {
            DEACTIVATION_LINK_LOSS -> {
                // 连接断开（手机离开读卡器）
            }
            DEACTIVATION_DESELECTED -> {
                // 被其他应用选中
            }
        }
    }

    /**
     * 设置当前激活的卡片（由 EmulateViewModel 调用）
     */
    fun setActiveCard(card: HillCardInfo?) {
        activeCard = card
        authenticatedSectors.clear()
    }

    /**
     * 获取当前激活的卡片
     */
    fun getActiveCard(): HillCardInfo? = activeCard
}
