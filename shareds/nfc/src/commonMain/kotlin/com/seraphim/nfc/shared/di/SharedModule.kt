package com.seraphim.nfc.shared.di

import com.seraphim.nfc.shared.data.db.database.KeyWorldDatabase
import com.seraphim.nfc.shared.data.repository.HillCardRepository
import com.seraphim.nfc.shared.domain.crypto.CardDataEncryptor
import com.seraphim.nfc.shared.domain.crypto.CardDataEncryptorStub
import org.koin.dsl.module

val sharedModule = module {
    // 加密器
    single<CardDataEncryptor> { CardDataEncryptorStub() }

    // 数据库 & DAO（由 Android 侧通过 Room.databaseBuilder 提供实例）
    single { get<KeyWorldDatabase>().hillCardDao() }
    single { get<KeyWorldDatabase>().cardGroupDao() }

    // 仓库
    single { HillCardRepository(get(), get(), get()) }
}
