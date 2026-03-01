package com.seraphim.pokemon.shared.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 属性（Type）缓存 — 克制关系
 */
@Entity(tableName = "type")
data class TypeEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    @ColumnInfo(name = "names_json")
    val namesJson: String,               // JSON: List<LocalizedName>
    @ColumnInfo(name = "damage_relations_json")
    val damageRelationsJson: String,     // JSON: TypeDamageRelations
    @ColumnInfo(name = "pokemon_json")
    val pokemonJson: String,             // JSON: List<TypePokemonSlot>
    @ColumnInfo(name = "moves_json")
    val movesJson: String,               // JSON: List<NamedRef>
    @ColumnInfo(name = "generation_name")
    val generationName: String?,
    @ColumnInfo(name = "last_updated_at")
    val lastUpdatedAt: Long,
)
