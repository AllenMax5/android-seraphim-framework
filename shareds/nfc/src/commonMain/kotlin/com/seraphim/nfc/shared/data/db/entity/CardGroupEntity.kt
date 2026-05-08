package com.seraphim.nfc.shared.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

/**
 * 卡片分组实体
 */
@Entity(tableName = "card_groups")
data class CardGroupEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val color: String,
    val sortOrder: Int = 0,
    val createdAt: Instant,
    val updatedAt: Instant,
)
