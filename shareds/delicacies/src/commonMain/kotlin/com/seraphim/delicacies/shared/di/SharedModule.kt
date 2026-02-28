package com.seraphim.delicacies.shared.di

import com.seraphim.delicacies.shared.data.db.DelicaciesDatabase
import com.seraphim.delicacies.shared.data.repository.CheckInRepository
import com.seraphim.delicacies.shared.data.repository.CheckInRepositoryImpl
import com.seraphim.delicacies.shared.domain.usecase.CheckInUseCase
import com.seraphim.delicacies.shared.domain.usecase.GetMonthlyRecordsUseCase
import com.seraphim.delicacies.shared.domain.usecase.GetMonthlyStatsUseCase
import com.seraphim.delicacies.shared.domain.usecase.UpdateCheckInUseCase
import org.koin.dsl.module

val sharedModule = module {
    // DAO
    single { get<DelicaciesDatabase>().checkInDao() }

    // Repository
    single<CheckInRepository> { CheckInRepositoryImpl(get()) }

    // Use Cases
    factory { CheckInUseCase(get()) }
    factory { GetMonthlyRecordsUseCase(get()) }
    factory { GetMonthlyStatsUseCase(get()) }
    factory { UpdateCheckInUseCase(get()) }
}
