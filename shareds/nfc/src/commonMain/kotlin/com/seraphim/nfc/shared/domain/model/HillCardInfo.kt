package com.seraphim.nfc.shared.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class HillCardInfo(
    val id: String,
    val uid: String,
    val cardType: CardType,
    val sectors: List<SectorData>,
    val manufacturer: String,
    val readAt: Instant,
    val name: String = "未命名卡片",
    val group: String = "未分组",
    val note: String = "",
    val isEncrypted: Boolean = false,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)

@Serializable
enum class CardType {
    MIFARE_CLASSIC_1K,
    MIFARE_CLASSIC_4K,
    DESFIRE,
    HILL_PROPRIETARY,
    UNKNOWN,
}

@Serializable
data class SectorData(
    val sectorIndex: Int,
    val blocks: List<BlockData>,
    val keyA: String? = null,
    val keyB: String? = null,
    val accessBits: String? = null,
)

@Serializable
data class BlockData(
    val blockIndex: Int,
    val data: String,
    val isTrailer: Boolean = false,
)
