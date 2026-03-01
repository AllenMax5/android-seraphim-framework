package com.seraphim.pokemon.shared.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 宝可梦详情 — 完整数据缓存
 */
@Entity(tableName = "pokemon_detail")
data class PokemonDetailEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val height: Int,     // 单位: 分米
    val weight: Int,     // 单位: 百克
    @ColumnInfo(name = "base_experience")
    val baseExperience: Int?,
    @ColumnInfo(name = "stats_json")
    val statsJson: String,       // JSON: List<StatValue>
    @ColumnInfo(name = "abilities_json")
    val abilitiesJson: String,   // JSON: List<AbilitySlot>
    @ColumnInfo(name = "types_json")
    val typesJson: String,       // JSON: List<PokemonTypeSlot>
    @ColumnInfo(name = "sprites_json")
    val spritesJson: String,     // JSON: Sprites
    @ColumnInfo(name = "cries_json")
    val criesJson: String?,      // JSON: Cries
    @ColumnInfo(name = "species_id")
    val speciesId: Int?,
    @ColumnInfo(name = "moves_json")
    val movesJson: String,       // JSON: List<PokemonMoveSlot>
    @ColumnInfo(name = "forms_json")
    val formsJson: String,       // JSON: List<NamedRef>
    @ColumnInfo(name = "last_updated_at")
    val lastUpdatedAt: Long,
)
