package com.seraphim.pokemon.shared.di

import com.seraphim.pokemon.shared.data.db.PokemonDatabase
import com.seraphim.pokemon.shared.data.db.getDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedPlatformModule = module {
    single<PokemonDatabase> {
        getDatabaseBuilder(androidContext()).build()
    }
}
