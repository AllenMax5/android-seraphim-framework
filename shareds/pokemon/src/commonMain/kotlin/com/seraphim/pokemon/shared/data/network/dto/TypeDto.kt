package com.seraphim.pokemon.shared.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── GET /api/v2/type/{id} ──────────────────────────────────────

@Serializable
data class TypeDto(
    val id: Int,
    val name: String,
    @SerialName("damage_relations") val damageRelations: TypeDamageRelationsDto,
    @SerialName("game_indices") val gameIndices: List<TypeGameIndex> = emptyList(),
    val generation: NamedApiResource? = null,
    @SerialName("move_damage_class") val moveDamageClass: NamedApiResource? = null,
    val names: List<LocalizedName> = emptyList(),
    val pokemon: List<TypePokemonSlot> = emptyList(),
    val moves: List<NamedApiResource> = emptyList(),
)

@Serializable
data class TypeDamageRelationsDto(
    @SerialName("no_damage_to") val noDamageTo: List<NamedApiResource>,
    @SerialName("half_damage_to") val halfDamageTo: List<NamedApiResource>,
    @SerialName("double_damage_to") val doubleDamageTo: List<NamedApiResource>,
    @SerialName("no_damage_from") val noDamageFrom: List<NamedApiResource>,
    @SerialName("half_damage_from") val halfDamageFrom: List<NamedApiResource>,
    @SerialName("double_damage_from") val doubleDamageFrom: List<NamedApiResource>,
)

@Serializable
data class TypePokemonSlot(
    val slot: Int,
    val pokemon: NamedApiResource,
)

@Serializable
data class TypeGameIndex(
    @SerialName("game_index") val gameIndex: Int,
    val generation: NamedApiResource,
)
