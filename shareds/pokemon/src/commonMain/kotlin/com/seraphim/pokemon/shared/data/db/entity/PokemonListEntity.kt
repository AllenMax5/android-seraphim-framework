package com.seraphim.pokemon.shared.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 宝可梦列表项 — 轻量级缓存，用于图鉴列表展示
 */
@Entity(tableName = "pokemon_list")
data class PokemonListEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    @ColumnInfo(name = "sprite_url")
    val spriteUrl: String?,
    @ColumnInfo(name = "types_json")
    val typesJson: String, // JSON: List<PokemonTypeSlot>
    val order: Int,
    @ColumnInfo(name = "last_updated_at")
    val lastUpdatedAt: Long, // epoch millis
)
