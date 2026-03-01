package com.seraphim.pokemon.shared.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 进化链缓存 — 存储整条进化链的 JSON
 */
@Entity(tableName = "evolution_chain")
data class EvolutionChainEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "chain_json")
    val chainJson: String, // JSON: ChainLink (递归结构)
    @ColumnInfo(name = "last_updated_at")
    val lastUpdatedAt: Long,
)
