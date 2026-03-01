package com.seraphim.pokemon.shared.domain.model

/**
 * 宝可梦详情 — UI 展示用完整模型
 */
data class PokemonDetail(
    val id: Int,
    val name: String,
    val height: Float,          // 米
    val weight: Float,          // 千克
    val baseExperience: Int?,
    val stats: List<StatValue>,
    val abilities: List<AbilityRef>,
    val types: List<PokemonTypeRef>,
    val sprites: Sprites,
    val cries: Cries?,
    val speciesId: Int?,
    val isFavorite: Boolean = false,
)

data class StatValue(
    val name: String,
    val baseStat: Int,
    val effort: Int,
) {
    companion object {
        const val HP = "hp"
        const val ATTACK = "attack"
        const val DEFENSE = "defense"
        const val SPECIAL_ATTACK = "special-attack"
        const val SPECIAL_DEFENSE = "special-defense"
        const val SPEED = "speed"
    }
}

val PokemonDetail.totalBaseStat: Int
    get() = stats.sumOf { it.baseStat }

data class AbilityRef(
    val name: String,
    val id: Int,
    val isHidden: Boolean,
    val slot: Int,
)

data class Sprites(
    val frontDefault: String?,
    val frontShiny: String?,
    val frontFemale: String?,
    val frontShinyFemale: String?,
    val backDefault: String?,
    val backShiny: String?,
    val backFemale: String?,
    val backShinyFemale: String?,
)

data class Cries(
    val latest: String?,
    val legacy: String?,
)
