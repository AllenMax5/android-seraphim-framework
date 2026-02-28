package com.seraphim.delicacies.shared.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class CheckInRecord(
    val id: Long = 0,
    val date: LocalDate,
    val mealType: MealType,
    val checkedIn: Boolean = true,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
