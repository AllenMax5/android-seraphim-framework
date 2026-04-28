package com.seraphim.delicacies.shared.domain.model

import kotlinx.datetime.LocalDate

data class DayCheckInStatus(
    val date: LocalDate,
    val lunchCheckedIn: Boolean = false,
    val dinnerCheckedIn: Boolean = false,
) {
    val checkInCount: Int
        get() = (if (lunchCheckedIn) 1 else 0) + (if (dinnerCheckedIn) 1 else 0)

    /**
     * 获取指定餐次的签到状态
     */
    fun isCheckedIn(mealType: MealType): Boolean = when (mealType) {
        MealType.LUNCH -> lunchCheckedIn
        MealType.DINNER -> dinnerCheckedIn
    }
}
