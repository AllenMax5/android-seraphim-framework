package com.seraphim.nfc.shared.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

/**
 * 门禁卡实体
 *
 * sectorsJson 存储加密后的扇区数据 JSON
 */
@Entity(
    tableName = "hill_cards",
    foreignKeys = [
        ForeignKey(
            entity = CardGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.SET_NULL,
        )
    ],
    indices = [
        Index(value = ["groupId"]),
        Index(value = ["uid"]),
        Index(value = ["name"]),
    ]
)
data class HillCardEntity(
    @PrimaryKey
    val id: String,
    val uid: String,
    val cardType: String,
    val name: String,
    val groupId: String? = null,
    val note: String = "",
    val sectorsJson: String,
    val manufacturer: String = "",
    val readAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
)
