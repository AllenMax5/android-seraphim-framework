package com.seraphim.app.nfc.nfc

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.provider.Settings
import com.seraphim.core.permissions.PermissionHelper
import com.seraphim.nfc.shared.domain.model.*
import kotlinx.datetime.Instant

/**
 * NFC 读取封装类
 *
 * 负责：
 * - 检测 NFC 状态（委托 PermissionHelper）
 * - 读取 MIFARE Classic 卡片完整数据
 * - 扇区认证与数据解析
 *
 * 官方文档：https://developer.android.com/develop/connectivity/nfc/nfc
 */
class NfcReader(
    private val context: Context,
) {

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

    /**
     * 检查 NFC 硬件是否可用
     * 委托 PermissionHelper 统一检测
     */
    fun isNfcAvailable(): Boolean = PermissionHelper.isNfcHardwareAvailable(context)

    /**
     * 检查 NFC 是否已开启
     * 委托 PermissionHelper 统一检测
     */
    fun isNfcEnabled(): Boolean = PermissionHelper.isNfcEnabled(context)

    /**
     * 跳转到系统 NFC 设置
     */
    fun openNfcSettings() {
        context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    /**
     * 读取 NFC 标签
     */
    fun readCard(tag: Tag): HillCardInfo? {
        val mifare = MifareClassic.get(tag) ?: return null

        return try {
            mifare.connect()

            val cardType = when (mifare.type) {
                MifareClassic.TYPE_CLASSIC -> CardType.MIFARE_CLASSIC_1K
                MifareClassic.TYPE_PLUS -> CardType.MIFARE_CLASSIC_4K
                else -> CardType.UNKNOWN
            }

            val sectors = readAllSectors(mifare)
            val uid = tag.id?.toHexString() ?: "Unknown"

            HillCardInfo(
                id = generateCardId(),
                uid = uid,
                cardType = cardType,
                sectors = sectors,
                manufacturer = tag.techList?.joinToString() ?: "Unknown",
                readAt = Instant.fromEpochMilliseconds(java.lang.System.currentTimeMillis()),
                name = "未命名卡片",
                group = "未分组",
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                mifare.close()
            } catch (_: Exception) {
            }
        }
    }

    /**
     * 读取所有扇区
     */
    private fun readAllSectors(mifare: MifareClassic): List<SectorData> {
        val sectors = mutableListOf<SectorData>()
        val defaultKeys = listOf(
            MifareClassic.KEY_DEFAULT,
            MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY,
            MifareClassic.KEY_NFC_FORUM,
        )

        for (sectorIndex in 0 until mifare.sectorCount) {
            var sectorRead = false

            for (key in defaultKeys) {
                if (mifare.authenticateSectorWithKeyA(sectorIndex, key)) {
                    val blocks = readSectorBlocks(mifare, sectorIndex)
                    sectors.add(
                        SectorData(
                            sectorIndex = sectorIndex,
                            blocks = blocks,
                            keyA = key.toHexString(),
                        ),
                    )
                    sectorRead = true
                    break
                }
            }

            if (!sectorRead) {
                // 记录无法读取的扇区
                sectors.add(
                    SectorData(
                        sectorIndex = sectorIndex,
                        blocks = emptyList(),
                        keyA = null,
                    ),
                )
            }
        }

        return sectors
    }

    /**
     * 读取指定扇区的所有块
     */
    private fun readSectorBlocks(mifare: MifareClassic, sectorIndex: Int): List<BlockData> {
        val blocks = mutableListOf<BlockData>()
        val blockCount = mifare.getBlockCountInSector(sectorIndex)
        val firstBlock = mifare.sectorToBlock(sectorIndex)

        for (blockOffset in 0 until blockCount) {
            val blockIndex = firstBlock + blockOffset
            val data = mifare.readBlock(blockIndex)
            blocks.add(
                BlockData(
                    blockIndex = blockIndex,
                    data = data.toHexString(),
                    isTrailer = blockOffset == blockCount - 1,
                ),
            )
        }

        return blocks
    }

    /**
     * 字节数组转 HEX 字符串
     */
    private fun ByteArray.toHexString(): String {
        return joinToString(":") { "%02X".format(it) }
    }

    private fun generateCardId(): String {
        return java.util.UUID.randomUUID().toString()
    }
}
