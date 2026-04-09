package com.seraphim.literacy.shared.model

import kotlinx.serialization.Serializable

/**
 * 学生作品
 */
@Serializable
data class StudentWork(
    val workId: String,
    val studentId: String,
    val title: String,
    val description: String,
    val type: WorkType,
    val files: List<String> = emptyList(),
    val wingId: String,
    val status: WorkStatus = WorkStatus.PENDING,
    val score: Int? = null,
    val feedback: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
