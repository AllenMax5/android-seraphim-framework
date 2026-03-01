package com.seraphim.pokemon.shared.data.db.mapper

import com.seraphim.pokemon.shared.data.db.entity.EvolutionChainEntity
import com.seraphim.pokemon.shared.data.db.entity.PokemonDetailEntity
import com.seraphim.pokemon.shared.data.db.entity.PokemonListEntity
import com.seraphim.pokemon.shared.data.db.entity.PokemonSpeciesEntity
import com.seraphim.pokemon.shared.data.db.entity.TypeEntity
import com.seraphim.pokemon.shared.data.network.dto.EvolutionChainDto
import com.seraphim.pokemon.shared.data.network.dto.PokemonDto
import com.seraphim.pokemon.shared.data.network.dto.PokemonSpeciesDto
import com.seraphim.pokemon.shared.data.network.dto.TypeDto
import kotlinx.serialization.json.Json

private val mapper = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

// ─── Pokemon DTO → Entity ───────────────────────────────────────

fun PokemonDto.toListEntity(now: Long): PokemonListEntity = PokemonListEntity(
    id = id,
    name = name,
    spriteUrl = sprites.frontDefault,
    typesJson = mapper.encodeToString(types),
    order = order,
    lastUpdatedAt = now,
)

fun PokemonDto.toDetailEntity(now: Long): PokemonDetailEntity = PokemonDetailEntity(
    id = id,
    name = name,
    height = height,
    weight = weight,
    baseExperience = baseExperience,
    statsJson = mapper.encodeToString(stats),
    abilitiesJson = mapper.encodeToString(abilities),
    typesJson = mapper.encodeToString(types),
    spritesJson = mapper.encodeToString(sprites),
    criesJson = cries?.let { mapper.encodeToString(it) },
    speciesId = species.id,
    movesJson = mapper.encodeToString(moves),
    formsJson = mapper.encodeToString(forms),
    lastUpdatedAt = now,
)

// ─── Species DTO → Entity ───────────────────────────────────────

fun PokemonSpeciesDto.toEntity(now: Long): PokemonSpeciesEntity = PokemonSpeciesEntity(
    id = id,
    name = name,
    namesJson = mapper.encodeToString(names),
    generaJson = mapper.encodeToString(genera),
    flavorTextsJson = mapper.encodeToString(flavorTextEntries),
    evolutionChainId = evolutionChain?.id,
    generationName = generation?.name,
    isLegendary = isLegendary,
    isMythical = isMythical,
    isBaby = isBaby,
    genderRate = genderRate,
    captureRate = captureRate,
    baseHappiness = baseHappiness,
    color = color?.name,
    shape = shape?.name,
    habitat = habitat?.name,
    eggGroupsJson = mapper.encodeToString(eggGroups.map { it.name }),
    evolvesFromSpeciesId = evolvesFromSpecies?.id,
    lastUpdatedAt = now,
)

// ─── Evolution Chain DTO → Entity ───────────────────────────────

fun EvolutionChainDto.toEntity(now: Long): EvolutionChainEntity = EvolutionChainEntity(
    id = id,
    chainJson = mapper.encodeToString(chain),
    lastUpdatedAt = now,
)

// ─── Type DTO → Entity ─────────────────────────────────────────

fun TypeDto.toEntity(now: Long): TypeEntity = TypeEntity(
    id = id,
    name = name,
    namesJson = mapper.encodeToString(names),
    damageRelationsJson = mapper.encodeToString(damageRelations),
    pokemonJson = mapper.encodeToString(pokemon),
    movesJson = mapper.encodeToString(moves),
    generationName = generation?.name,
    lastUpdatedAt = now,
)

// ─── Cache expiry helpers ───────────────────────────────────────

private const val THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000
private const val SEVEN_DAYS_MS = 7L * 24 * 60 * 60 * 1000

fun isExpired(lastUpdatedAt: Long, now: Long, ttlMs: Long = THIRTY_DAYS_MS): Boolean =
    (now - lastUpdatedAt) > ttlMs

fun isListExpired(lastUpdatedAt: Long, now: Long): Boolean =
    isExpired(lastUpdatedAt, now, SEVEN_DAYS_MS)
