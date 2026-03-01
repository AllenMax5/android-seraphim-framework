package com.seraphim.pokemon.shared.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── Common Reference ──────────────────────────────────────────

@Serializable
data class NamedApiResource(
    val name: String,
    val url: String,
) {
    /** 从 URL 中提取 ID，例: "https://pokeapi.co/api/v2/pokemon/25/" → 25 */
    val id: Int
        get() = url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
}

@Serializable
data class ApiResource(
    val url: String,
) {
    val id: Int
        get() = url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
}

@Serializable
data class NamedApiResourceList(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NamedApiResource>,
)

// ─── Localized Name / FlavorText ────────────────────────────────

@Serializable
data class LocalizedName(
    val name: String,
    val language: NamedApiResource,
)

@Serializable
data class FlavorTextEntry(
    @SerialName("flavor_text") val flavorText: String,
    val language: NamedApiResource,
    val version: NamedApiResource? = null,
)

@Serializable
data class Genus(
    val genus: String,
    val language: NamedApiResource,
)

@Serializable
data class VerboseEffect(
    val effect: String,
    @SerialName("short_effect") val shortEffect: String,
    val language: NamedApiResource,
)
