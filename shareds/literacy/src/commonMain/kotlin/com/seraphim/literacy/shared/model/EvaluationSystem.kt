package com.seraphim.literacy.shared.model

import kotlinx.serialization.Serializable

/**
 * 六翼维度
 */
@Serializable
data class Wing(
    val wingId: String,
    val name: String,
    val icon: String,
    val color: String,
    val description: String
)

/**
 * 评价体系
 */
@Serializable
data class EvaluationSystem(
    val systemId: String,
    val schoolId: String,
    val name: String,
    val sixWings: List<Wing>,
    val fourDimensions: List<String> = listOf("学习能力", "实践能力", "创新能力", "合作能力"),
    val threeLevels: List<String> = listOf("基础达成", "良好发展", "卓越表现"),
    val isDefault: Boolean = false
)
