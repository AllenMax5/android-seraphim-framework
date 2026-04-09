package com.seraphim.literacy.shared.model

/**
 * 用户角色
 */
enum class UserRole {
    TEACHER,    // 教师
    STUDENT,    // 学生
    PARENT,     // 家长
    ADMIN       // 管理员
}

/**
 * 用户状态
 */
enum class UserStatus {
    ACTIVE,     // 活跃
    INACTIVE    // 非活跃
}

/**
 * 评价场景类型
 */
enum class SceneType {
    DAILY,      // 日常层
    ACTIVITY,   // 活动层
    PROJECT,    // 项目层
    PRACTICE    // 实践层
}

/**
 * 作品类型
 */
enum class WorkType {
    IMAGE,      // 图片
    VIDEO,      // 视频
    ARTICLE     // 文章
}

/**
 * 作品审核状态
 */
enum class WorkStatus {
    PENDING,    // 待审核
    APPROVED,   // 已通过
    REJECTED    // 已拒绝
}

/**
 * 进阶等级
 */
enum class AdvanceLevel(val level: Int, val displayName: String) {
    BASIC(1, "基础达成"),
    GOOD(2, "良好发展"),
    EXCELLENT(3, "卓越表现")
}
