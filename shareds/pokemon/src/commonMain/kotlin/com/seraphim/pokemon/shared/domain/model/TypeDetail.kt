package com.seraphim.pokemon.shared.domain.model

/**
 * 属性详情 — 克制关系
 */
data class TypeDetail(
    val id: Int,
    val name: String,
    val localizedName: String?,
    val damageRelations: DamageRelations,
)

/**
 * 伤害倍率关系
 */
data class DamageRelations(
    val doubleDamageTo: List<TypeRef>,
    val halfDamageTo: List<TypeRef>,
    val noDamageTo: List<TypeRef>,
    val doubleDamageFrom: List<TypeRef>,
    val halfDamageFrom: List<TypeRef>,
    val noDamageFrom: List<TypeRef>,
)

/**
 * 属性引用
 */
data class TypeRef(
    val name: String,
    val id: Int,
)
