package com.seraphim.pokemon.shared.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户收藏表 — 纯本地
 */
@Entity(tableName = "pokemon_favorite")
data class PokemonFavoriteEntity(
    @PrimaryKey
    @ColumnInfo(name = "pokemon_id")
    val pokemonId: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: Long, // epoch millis
)
