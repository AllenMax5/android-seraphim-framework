package com.seraphim.delicacies.shared.domain.model

data class MonthlyStats(
    val totalCount: Int = 0,
    val lunchCount: Int = 0,
    val dinnerCount: Int = 0,
    val maxCount: Int = DEFAULT_MONTHLY_LIMIT,
) {
    val remaining: Int get() = (maxCount - totalCount).coerceAtLeast(0)
    val isLimitReached: Boolean get() = totalCount >= maxCount
    val progress: Float get() = if (maxCount > 0) totalCount.toFloat() / maxCount else 0f

    companion object {
        const val DEFAULT_MONTHLY_LIMIT = 20
    }
}
