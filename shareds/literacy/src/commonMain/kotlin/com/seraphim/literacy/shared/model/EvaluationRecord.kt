package com.seraphim.literacy.shared.model

import kotlinx.serialization.Serializable

/**
 * 评价记录
 */
@Serializable
data class EvaluationRecord(
    val recordId: String,
    val studentId: String,
    val teacherId: String,
    val sceneId: String,
    val sceneType: SceneType,
    val wingId: String,
    val score: Int,           // 具体分数
    val level: Int,           // 1, 2, 3 对应三级进阶
    val comment: String? = null,
    val images: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
