package com.seraphim.pokemon.shared.domain.model

/**
 * 宝可梦物种信息 — 图鉴文本、分类、进化等
 */
data class PokemonSpecies(
    val id: Int,
    val name: String,
    val localizedNames: Map<String, String>,     // language → name
    val genus: String?,                           // 分类（如 "种子宝可梦"）
    val flavorText: String?,                      // 图鉴描述（优先中文 → 英文）
    val evolutionChainId: Int?,
    val generationName: String?,
    val isLegendary: Boolean,
    val isMythical: Boolean,
    val isBaby: Boolean,
    val genderRate: Int,                          // -1 = 无性别, 0~8 = 雌性概率 1/8
    val captureRate: Int,
    val baseHappiness: Int?,
    val color: String?,
    val shape: String?,
    val habitat: String?,
    val eggGroups: List<String>,
    val evolvesFromSpeciesId: Int?,
)
