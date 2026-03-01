package com.seraphim.pokemon.shared.di

import com.seraphim.core.storage.db.AndroidDatabaseFactory
import com.seraphim.core.storage.db.DatabaseBuilder
import com.seraphim.core.storage.db.DatabaseFactory
import com.seraphim.pokemon.shared.data.db.PokemonDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedPlatformModule = module {
    single<DatabaseFactory> { AndroidDatabaseFactory(androidContext()) }
    single<PokemonDatabase> {
        DatabaseBuilder.build(
            factory = get(),
            klass = PokemonDatabase::class,
            name = "pokemon.db",
        )
    }
}
