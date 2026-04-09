package com.seraphim.literacy.shared.model

import kotlinx.serialization.Serializable

/**
 * 用户实体
 */
@Serializable
data class User(
    val userId: String,
    val role: UserRole,
    val name: String,
    val avatar: String? = null,
    val schoolId: String,
    val classId: String? = null,
    val grade: Int? = null,
    val status: UserStatus = UserStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
)
