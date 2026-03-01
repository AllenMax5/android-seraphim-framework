package com.seraphim.pokemon.shared.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── GET /api/v2/pokemon/{id} ───────────────────────────────────

@Serializable
data class PokemonDto(
    val id: Int,
    val name: String,
    @SerialName("base_experience") val baseExperience: Int? = null,
    val height: Int,
    val weight: Int,
    val order: Int,
    val abilities: List<PokemonAbilitySlot>,
    val forms: List<NamedApiResource>,
    val moves: List<PokemonMoveSlot>,
    val species: NamedApiResource,
    val sprites: SpritesDto,
    val cries: CriesDto? = null,
    val stats: List<PokemonStatSlot>,
    val types: List<PokemonTypeSlot>,
)

@Serializable
data class PokemonAbilitySlot(
    @SerialName("is_hidden") val isHidden: Boolean,
    val slot: Int,
    val ability: NamedApiResource,
)

@Serializable
data class PokemonMoveSlot(
    val move: NamedApiResource,
    @SerialName("version_group_details") val versionGroupDetails: List<PokemonMoveVersionDetail>,
)

@Serializable
data class PokemonMoveVersionDetail(
    @SerialName("move_learn_method") val moveLearnMethod: NamedApiResource,
    @SerialName("version_group") val versionGroup: NamedApiResource,
    @SerialName("level_learned_at") val levelLearnedAt: Int,
)

@Serializable
data class PokemonStatSlot(
    @SerialName("base_stat") val baseStat: Int,
    val effort: Int,
    val stat: NamedApiResource,
)

@Serializable
data class PokemonTypeSlot(
    val slot: Int,
    val type: NamedApiResource,
)

@Serializable
data class SpritesDto(
    @SerialName("front_default") val frontDefault: String? = null,
    @SerialName("front_shiny") val frontShiny: String? = null,
    @SerialName("front_female") val frontFemale: String? = null,
    @SerialName("front_shiny_female") val frontShinyFemale: String? = null,
    @SerialName("back_default") val backDefault: String? = null,
    @SerialName("back_shiny") val backShiny: String? = null,
    @SerialName("back_female") val backFemale: String? = null,
    @SerialName("back_shiny_female") val backShinyFemale: String? = null,
)

@Serializable
data class CriesDto(
    val latest: String? = null,
    val legacy: String? = null,
)
