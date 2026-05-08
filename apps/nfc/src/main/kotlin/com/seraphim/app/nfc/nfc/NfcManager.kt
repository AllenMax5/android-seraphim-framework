package com.seraphim.app.nfc.nfc

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.NfcA
import android.os.Build
import android.os.Bundle
import com.seraphim.core.permissions.PermissionHelper
import com.seraphim.nfc.shared.domain.model.BlockData
import com.seraphim.nfc.shared.domain.model.CardType
import com.seraphim.nfc.shared.domain.model.HillCardInfo
import com.seraphim.nfc.shared.domain.model.SectorData
import kotlinx.datetime.Instant
import java.util.UUID

/**
 * NFC 管理器 — 使用 ReaderMode 前台读卡（Android 4.4+）
 *
 * 优势：
 * - 不依赖 PendingIntent，完全绕过 Android 14+ BAL 限制
 * - 在 Activity 前台时独占 NFC 控制器
 * - 回调直接返回 Tag 对象，无需 Intent 解析
 *
 * 官方文档：https://developer.android.com/develop/connectivity/nfc/advanced-nfc
 */
class NfcManager(
    private val activity: Activity,
    private val onTagDiscovered: (HillCardInfo) -> Unit,
    private val onError: (String) -> Unit,
) {

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)
    private val readerCallback = NfcAdapter.ReaderCallback { tag ->
        activity.runOnUiThread {
            try {
                val cardInfo = readTag(tag)
                if (cardInfo != null) {
                    onTagDiscovered(cardInfo)
                } else {
                    onError("无法识别此 NFC 标签")
                }
            } catch (e: Exception) {
                onError("读卡错误: ${e.message}")
            }
        }
    }

    /**
     * 启用 ReaderMode（在 onResume 中调用）
     *
     * FLAG_READER_NFC_A | FLAG_READER_NFC_B | FLAG_READER_SKIP_NDEF_CHECK
     * 跳过 NDEF 检查，直接回调原始 Tag，适合门禁卡读取
     */
    fun enableReaderMode() {
        if (!PermissionHelper.isNfcEnabled(activity)) {
            onError("NFC 未启用")
            return
        }

        val flags = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK

        val extras = Bundle().apply {
            // 可选：设置读卡超时（毫秒）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)
            }
        }

        nfcAdapter?.enableReaderMode(activity, readerCallback, flags, extras)
    }

    /**
     * 禁用 ReaderMode（在 onPause 中调用）
     */
    fun disableReaderMode() {
        nfcAdapter?.disableReaderMode(activity)
    }

    /**
     * 读取 NFC 标签并解析为 HillCardInfo
     */
    private fun readTag(tag: Tag): HillCardInfo? {
        val uid = bytesToHex(tag.id) ?: return null

        // 尝试 MIFARE Classic
        val mifare = MifareClassic.get(tag)
        if (mifare != null) {
            return readMifareClassic(mifare, uid)
        }

        // 尝试 NfcA（其他类型）
        val nfcA = NfcA.get(tag)
        if (nfcA != null) {
            return HillCardInfo(
                id = UUID.randomUUID().toString(),
                uid = uid,
                name = "未知卡片",
                cardType = CardType.UNKNOWN,
                sectors = emptyList(),
                readAt = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
                manufacturer = nfcA.atqa?.let { bytesToHex(it) } ?: "Unknown",
                group = "未分组",
                note = "SAK: ${nfcA.sak}, ATQA: ${nfcA.atqa?.let { bytesToHex(it) }}",
            )
        }

        return null
    }

    /**
     * 读取 MIFARE Classic 卡片（1K / 4K）
     */
    private fun readMifareClassic(mifare: MifareClassic, uid: String): HillCardInfo? {
        return try {
            mifare.connect()

            val type = when (mifare.type) {
                MifareClassic.TYPE_CLASSIC -> CardType.MIFARE_CLASSIC_1K
                MifareClassic.TYPE_PLUS -> CardType.MIFARE_CLASSIC_4K
                else -> CardType.UNKNOWN
            }

            val sectorCount = mifare.sectorCount
            val sectors = mutableListOf<SectorData>()

            // 尝试用默认密钥读取所有扇区
            val defaultKey = MifareClassic.KEY_DEFAULT

            for (sectorIndex in 0 until sectorCount) {
                try {
                    val auth = mifare.authenticateSectorWithKeyA(sectorIndex, defaultKey)
                    if (!auth) continue

                    val blockCount = mifare.getBlockCountInSector(sectorIndex)
                    val firstBlock = mifare.sectorToBlock(sectorIndex)
                    val blocks = mutableListOf<BlockData>()

                    for (blockOffset in 0 until blockCount) {
                        val blockIndex = firstBlock + blockOffset
                        val data = mifare.readBlock(blockIndex)
                        val isTrailer = blockOffset == blockCount - 1

                        blocks.add(
                            BlockData(
                                blockIndex = blockIndex,
                                data = bytesToHex(data) ?: "00",
                                isTrailer = isTrailer,
                            )
                        )
                    }

                    // 尾块解析密钥和访问位
                    val trailerBlock = blocks.lastOrNull()
                    val keyA = if (trailerBlock != null && trailerBlock.data.length >= 12) {
                        trailerBlock.data.substring(0, 12)
                    } else null
                    val keyB = if (trailerBlock != null && trailerBlock.data.length >= 24) {
                        trailerBlock.data.substring(20, 32)
                    } else null
                    val accessBits = if (trailerBlock != null && trailerBlock.data.length >= 20) {
                        trailerBlock.data.substring(12, 20)
                    } else null

                    sectors.add(
                        SectorData(
                            sectorIndex = sectorIndex,
                            blocks = blocks,
                            keyA = keyA,
                            keyB = keyB,
                            accessBits = accessBits,
                        )
                    )
                } catch (e: Exception) {
                    // 扇区读取失败，记录空扇区
                    sectors.add(
                        SectorData(
                            sectorIndex = sectorIndex,
                            blocks = emptyList(),
                            keyA = null,
                            keyB = null,
                            accessBits = null,
                        )
                    )
                }
            }

            HillCardInfo(
                id = UUID.randomUUID().toString(),
                uid = uid,
                name = "MIFARE ${if (sectorCount == 16) "1K" else "4K"}",
                cardType = type,
                sectors = sectors,
                readAt = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
                manufacturer = "NXP",
                group = "未分组",
                note = "扇区数: $sectorCount, 已读: ${sectors.count { it.blocks.isNotEmpty() }}/${sectors.size}",
            )
        } catch (e: Exception) {
            onError("MIFARE 读取失败: ${e.message}")
            null
        } finally {
            try { mifare.close() } catch (_: Exception) {}
        }
    }

    /**
     * 字节数组转 HEX 字符串
     */
    private fun bytesToHex(bytes: ByteArray?): String? {
        if (bytes == null) return null
        return bytes.joinToString(":") { String.format("%02X", it) }
    }

    fun isNfcAvailable(): Boolean = PermissionHelper.isNfcHardwareAvailable(activity)
    fun isNfcEnabled(): Boolean = PermissionHelper.isNfcEnabled(activity)
}
