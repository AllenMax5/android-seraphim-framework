package com.seraphim.literacy.shared.model

import kotlinx.serialization.Serializable

/**
 * 学生雷达图数据（四维）
 */
@Serializable
data class RadarChartData(
    val studentId: String,
    val dimensions: List<DimensionScore>,
    val updateTime: Long = System.currentTimeMillis()
)

/**
 * 维度得分
 */
@Serializable
data class DimensionScore(
    val dimensionName: String,
    val score: Float,  // 0-100
    val maxScore: Float = 100f
)

/**
 * 六翼得分
 */
@Serializable
data class WingScore(
    val wingId: String,
    val wingName: String,
    val score: Float,  // 0-100
    val level: Int,    // 1, 2, 3
    val evaluationCount: Int = 0
)
