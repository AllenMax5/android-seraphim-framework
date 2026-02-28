package com.seraphim.delicacies.shared.data.db

import com.seraphim.delicacies.shared.domain.model.CheckInRecord
import com.seraphim.delicacies.shared.domain.model.MealType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

fun CheckInEntity.toDomainModel(): CheckInRecord = CheckInRecord(
    id = id,
    date = LocalDate.parse(date),
    mealType = MealType.valueOf(mealType),
    checkedIn = checkedIn,
    createdAt = LocalDateTime.parse(createdAt),
    updatedAt = LocalDateTime.parse(updatedAt),
)

fun CheckInRecord.toEntity(): CheckInEntity = CheckInEntity(
    id = id,
    date = date.toString(),
    mealType = mealType.name,
    checkedIn = checkedIn,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
)
