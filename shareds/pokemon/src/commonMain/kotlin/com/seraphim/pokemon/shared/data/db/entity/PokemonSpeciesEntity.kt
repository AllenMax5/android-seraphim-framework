package com.seraphim.pokemon.shared.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 宝可梦物种信息 — 图鉴描述、进化链引用、传说/幻兽标记等
 */
@Entity(tableName = "pokemon_species")
data class PokemonSpeciesEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    @ColumnInfo(name = "names_json")
    val namesJson: String,              // JSON: List<LocalizedName>
    @ColumnInfo(name = "genera_json")
    val generaJson: String,             // JSON: List<Genus>
    @ColumnInfo(name = "flavor_texts_json")
    val flavorTextsJson: String,        // JSON: List<FlavorText>
    @ColumnInfo(name = "evolution_chain_id")
    val evolutionChainId: Int?,
    @ColumnInfo(name = "generation_name")
    val generationName: String?,
    @ColumnInfo(name = "is_legendary")
    val isLegendary: Boolean,
    @ColumnInfo(name = "is_mythical")
    val isMythical: Boolean,
    @ColumnInfo(name = "is_baby")
    val isBaby: Boolean,
    @ColumnInfo(name = "gender_rate")
    val genderRate: Int,               // 1/8 概率，-1 = 无性别
    @ColumnInfo(name = "capture_rate")
    val captureRate: Int,
    @ColumnInfo(name = "base_happiness")
    val baseHappiness: Int?,
    val color: String?,
    val shape: String?,
    val habitat: String?,
    @ColumnInfo(name = "egg_groups_json")
    val eggGroupsJson: String,         // JSON: List<String>
    @ColumnInfo(name = "evolves_from_species_id")
    val evolvesFromSpeciesId: Int?,
    @ColumnInfo(name = "last_updated_at")
    val lastUpdatedAt: Long,
)
