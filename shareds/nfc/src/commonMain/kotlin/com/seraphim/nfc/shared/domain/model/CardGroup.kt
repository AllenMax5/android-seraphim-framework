package com.seraphim.nfc.shared.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 卡片分组
 */
@Serializable
data class CardGroup(
    val id: String,
    val name: String,
    val color: String,
    val sortOrder: Int = 0,
    val createdAt: Instant,
    val updatedAt: Instant,
)

/**
 * 预置分组
 */
val DefaultGroups = listOf(
    CardGroup("home", "家", "#4CAF50", 0, Instant.fromEpochMilliseconds(0), Instant.fromEpochMilliseconds(0)),
    CardGroup("company", "公司", "#FF9800", 1, Instant.fromEpochMilliseconds(0), Instant.fromEpochMilliseconds(0)),
    CardGroup("visitor", "访客", "#2196F3", 2, Instant.fromEpochMilliseconds(0), Instant.fromEpochMilliseconds(0)),
    CardGroup("ungrouped", "未分组", "#9C27B0", 3, Instant.fromEpochMilliseconds(0), Instant.fromEpochMilliseconds(0)),
)
