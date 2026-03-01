package com.seraphim.pokemon.shared.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── GET /api/v2/pokemon-species/{id} ───────────────────────────

@Serializable
data class PokemonSpeciesDto(
    val id: Int,
    val name: String,
    val order: Int,
    @SerialName("gender_rate") val genderRate: Int,
    @SerialName("capture_rate") val captureRate: Int,
    @SerialName("base_happiness") val baseHappiness: Int? = null,
    @SerialName("is_baby") val isBaby: Boolean,
    @SerialName("is_legendary") val isLegendary: Boolean,
    @SerialName("is_mythical") val isMythical: Boolean,
    @SerialName("growth_rate") val growthRate: NamedApiResource? = null,
    val names: List<LocalizedName>,
    val genera: List<Genus>,
    @SerialName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>,
    @SerialName("egg_groups") val eggGroups: List<NamedApiResource>,
    val color: NamedApiResource? = null,
    val shape: NamedApiResource? = null,
    @SerialName("evolves_from_species") val evolvesFromSpecies: NamedApiResource? = null,
    @SerialName("evolution_chain") val evolutionChain: ApiResource? = null,
    val habitat: NamedApiResource? = null,
    val generation: NamedApiResource? = null,
    val varieties: List<PokemonSpeciesVariety> = emptyList(),
)

@Serializable
data class PokemonSpeciesVariety(
    @SerialName("is_default") val isDefault: Boolean,
    val pokemon: NamedApiResource,
)
