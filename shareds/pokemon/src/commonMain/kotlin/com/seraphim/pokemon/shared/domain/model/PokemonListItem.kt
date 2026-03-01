package com.seraphim.pokemon.shared.domain.model

/**
 * 图鉴列表项 — UI 层使用的轻量模型
 */
data class PokemonListItem(
    val id: Int,
    val name: String,
    val spriteUrl: String?,
    val types: List<PokemonTypeRef>,
    val isFavorite: Boolean = false,
)

data class PokemonTypeRef(
    val slot: Int,
    val typeName: String,
    val typeId: Int,
)
