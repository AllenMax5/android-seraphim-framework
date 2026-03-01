package com.seraphim.pokemon.shared.domain.mapper

import com.seraphim.pokemon.shared.data.db.entity.EvolutionChainEntity
import com.seraphim.pokemon.shared.data.db.entity.PokemonDetailEntity
import com.seraphim.pokemon.shared.data.db.entity.PokemonListEntity
import com.seraphim.pokemon.shared.data.db.entity.PokemonSpeciesEntity
import com.seraphim.pokemon.shared.data.db.entity.TypeEntity
import com.seraphim.pokemon.shared.data.network.dto.ChainLinkDto
import com.seraphim.pokemon.shared.data.network.dto.CriesDto
import com.seraphim.pokemon.shared.data.network.dto.LocalizedName
import com.seraphim.pokemon.shared.data.network.dto.NamedApiResource
import com.seraphim.pokemon.shared.data.network.dto.PokemonAbilitySlot
import com.seraphim.pokemon.shared.data.network.dto.PokemonStatSlot
import com.seraphim.pokemon.shared.data.network.dto.PokemonTypeSlot
import com.seraphim.pokemon.shared.data.network.dto.SpritesDto
import com.seraphim.pokemon.shared.data.network.dto.TypeDamageRelationsDto
import com.seraphim.pokemon.shared.domain.model.AbilityRef
import com.seraphim.pokemon.shared.domain.model.Cries
import com.seraphim.pokemon.shared.domain.model.DamageRelations
import com.seraphim.pokemon.shared.domain.model.EvolutionChain
import com.seraphim.pokemon.shared.domain.model.EvolutionNode
import com.seraphim.pokemon.shared.domain.model.EvolutionTrigger
import com.seraphim.pokemon.shared.domain.model.PokemonDetail
import com.seraphim.pokemon.shared.domain.model.PokemonListItem
import com.seraphim.pokemon.shared.domain.model.PokemonSpecies
import com.seraphim.pokemon.shared.domain.model.PokemonTypeRef
import com.seraphim.pokemon.shared.domain.model.Sprites
import com.seraphim.pokemon.shared.domain.model.StatValue
import com.seraphim.pokemon.shared.domain.model.TypeDetail
import com.seraphim.pokemon.shared.domain.model.TypeRef
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

// ─── PokemonListEntity → PokemonListItem ────────────────────────

fun PokemonListEntity.toDomainModel(): PokemonListItem {
    val types: List<PokemonTypeSlot> = runCatching {
        json.decodeFromString<List<PokemonTypeSlot>>(typesJson)
    }.getOrDefault(emptyList())

    return PokemonListItem(
        id = id,
        name = name,
        spriteUrl = spriteUrl,
        types = types.map { it.toDomainType() },
    )
}

// ─── PokemonDetailEntity → PokemonDetail ────────────────────────

fun PokemonDetailEntity.toDomainModel(): PokemonDetail {
    val types: List<PokemonTypeSlot> = runCatching {
        json.decodeFromString<List<PokemonTypeSlot>>(typesJson)
    }.getOrDefault(emptyList())

    val stats: List<PokemonStatSlot> = runCatching {
        json.decodeFromString<List<PokemonStatSlot>>(statsJson)
    }.getOrDefault(emptyList())

    val abilities: List<PokemonAbilitySlot> = runCatching {
        json.decodeFromString<List<PokemonAbilitySlot>>(abilitiesJson)
    }.getOrDefault(emptyList())

    val sprites: SpritesDto = runCatching {
        json.decodeFromString<SpritesDto>(spritesJson)
    }.getOrDefault(SpritesDto())

    val cries: CriesDto? = criesJson?.let {
        runCatching { json.decodeFromString<CriesDto>(it) }.getOrNull()
    }

    return PokemonDetail(
        id = id,
        name = name,
        height = height / 10f,
        weight = weight / 10f,
        baseExperience = baseExperience,
        stats = stats.map {
            StatValue(
                name = it.stat.name,
                baseStat = it.baseStat,
                effort = it.effort,
            )
        },
        abilities = abilities.map {
            AbilityRef(
                name = it.ability.name,
                id = it.ability.id,
                isHidden = it.isHidden,
                slot = it.slot,
            )
        },
        types = types.map { it.toDomainType() },
        sprites = Sprites(
            frontDefault = sprites.frontDefault,
            frontShiny = sprites.frontShiny,
            frontFemale = sprites.frontFemale,
            frontShinyFemale = sprites.frontShinyFemale,
            backDefault = sprites.backDefault,
            backShiny = sprites.backShiny,
            backFemale = sprites.backFemale,
            backShinyFemale = sprites.backShinyFemale,
        ),
        cries = cries?.let { Cries(latest = it.latest, legacy = it.legacy) },
        speciesId = speciesId,
    )
}

// ─── PokemonSpeciesEntity → PokemonSpecies ─────────────────────

fun PokemonSpeciesEntity.toDomainModel(): PokemonSpecies {
    val names: List<LocalizedName> = runCatching {
        json.decodeFromString<List<LocalizedName>>(namesJson)
    }.getOrDefault(emptyList())

    val eggGroupNames: List<String> = runCatching {
        json.decodeFromString<List<String>>(eggGroupsJson)
    }.getOrDefault(emptyList())

    val localizedNameMap = names.associate { it.language.name to it.name }
    val genus = localizedNameMap["zh-Hans"]
        ?: localizedNameMap["zh-Hant"]
        ?: localizedNameMap["ja"]

    val generaList = runCatching {
        json.decodeFromString<List<com.seraphim.pokemon.shared.data.network.dto.Genus>>(generaJson)
    }.getOrDefault(emptyList())
    val genusText = generaList.firstOrNull { it.language.name == "zh-Hans" }?.genus
        ?: generaList.firstOrNull { it.language.name == "en" }?.genus

    val flavorTexts = runCatching {
        json.decodeFromString<List<com.seraphim.pokemon.shared.data.network.dto.FlavorTextEntry>>(
            flavorTextsJson
        )
    }.getOrDefault(emptyList())
    val flavorText = flavorTexts.firstOrNull { it.language.name == "zh-Hans" }?.flavorText
        ?: flavorTexts.firstOrNull { it.language.name == "en" }?.flavorText

    return PokemonSpecies(
        id = id,
        name = name,
        localizedNames = localizedNameMap,
        genus = genusText,
        flavorText = flavorText?.replace("\n", " ")?.replace("\u000c", " "),
        evolutionChainId = evolutionChainId,
        generationName = generationName,
        isLegendary = isLegendary,
        isMythical = isMythical,
        isBaby = isBaby,
        genderRate = genderRate,
        captureRate = captureRate,
        baseHappiness = baseHappiness,
        color = color,
        shape = shape,
        habitat = habitat,
        eggGroups = eggGroupNames,
        evolvesFromSpeciesId = evolvesFromSpeciesId,
    )
}

// ─── EvolutionChainEntity → EvolutionChain ──────────────────────

fun EvolutionChainEntity.toDomainModel(): EvolutionChain {
    val root: ChainLinkDto = json.decodeFromString(chainJson)
    return EvolutionChain(
        id = id,
        chain = root.toDomainNode(),
    )
}

private fun ChainLinkDto.toDomainNode(): EvolutionNode = EvolutionNode(
    speciesName = species.name,
    speciesId = species.id,
    isBaby = isBaby,
    evolutionDetails = evolutionDetails.map { detail ->
        EvolutionTrigger(
            triggerName = detail.trigger?.name,
            minLevel = detail.minLevel,
            itemName = detail.item?.name,
            heldItemName = detail.heldItem?.name,
            knownMoveName = detail.knownMove?.name,
            knownMoveTypeName = detail.knownMoveType?.name,
            locationName = detail.location?.name,
            minHappiness = detail.minHappiness,
            minBeauty = detail.minBeauty,
            minAffection = detail.minAffection,
            timeOfDay = detail.timeOfDay.ifEmpty { null },
            needsOverworldRain = detail.needsOverworldRain,
            gender = detail.gender,
        )
    },
    evolvesTo = evolvesTo.map { it.toDomainNode() },
)

// ─── TypeEntity → TypeDetail ────────────────────────────────────

fun TypeEntity.toDomainModel(): TypeDetail {
    val names: List<LocalizedName> = runCatching {
        json.decodeFromString<List<LocalizedName>>(namesJson)
    }.getOrDefault(emptyList())

    val damageRelations: TypeDamageRelationsDto = json.decodeFromString(damageRelationsJson)

    val localizedName = names.firstOrNull { it.language.name == "zh-Hans" }?.name
        ?: names.firstOrNull { it.language.name == "en" }?.name

    return TypeDetail(
        id = id,
        name = name,
        localizedName = localizedName,
        damageRelations = DamageRelations(
            doubleDamageTo = damageRelations.doubleDamageTo.toTypeRefs(),
            halfDamageTo = damageRelations.halfDamageTo.toTypeRefs(),
            noDamageTo = damageRelations.noDamageTo.toTypeRefs(),
            doubleDamageFrom = damageRelations.doubleDamageFrom.toTypeRefs(),
            halfDamageFrom = damageRelations.halfDamageFrom.toTypeRefs(),
            noDamageFrom = damageRelations.noDamageFrom.toTypeRefs(),
        ),
    )
}

// ─── Helpers ────────────────────────────────────────────────────

private fun PokemonTypeSlot.toDomainType() = PokemonTypeRef(
    slot = slot,
    typeName = type.name,
    typeId = type.id,
)

private fun List<NamedApiResource>.toTypeRefs() = map { TypeRef(name = it.name, id = it.id) }
