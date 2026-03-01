package com.seraphim.pokemon.shared.domain.model

/**
 * 进化链 — 树形结构，从基础形态递归至最终形态
 */
data class EvolutionChain(
    val id: Int,
    val chain: EvolutionNode,
)

/**
 * 进化节点
 */
data class EvolutionNode(
    val speciesName: String,
    val speciesId: Int,
    val isBaby: Boolean,
    val evolutionDetails: List<EvolutionTrigger>,
    val evolvesTo: List<EvolutionNode>,
)

/**
 * 进化条件
 */
data class EvolutionTrigger(
    val triggerName: String?,
    val minLevel: Int?,
    val itemName: String?,
    val heldItemName: String?,
    val knownMoveName: String?,
    val knownMoveTypeName: String?,
    val locationName: String?,
    val minHappiness: Int?,
    val minBeauty: Int?,
    val minAffection: Int?,
    val timeOfDay: String?,
    val needsOverworldRain: Boolean,
    val gender: Int?,
)
