package com.seraphim.pokemon.shared.di

import com.seraphim.pokemon.shared.data.db.PokemonDatabase
import com.seraphim.pokemon.shared.data.network.PokeApiService
import com.seraphim.pokemon.shared.data.network.PokeHttpClientFactory
import com.seraphim.pokemon.shared.data.repository.PokemonRepository
import com.seraphim.pokemon.shared.data.repository.PokemonRepositoryImpl
import org.koin.dsl.module

val sharedModule = module {
    // ─── Network ────────────────────────────────────────────────
    single { PokeHttpClientFactory.create() }
    single { PokeApiService(get()) }

    // ─── DAOs ───────────────────────────────────────────────────
    single { get<PokemonDatabase>().pokemonListDao() }
    single { get<PokemonDatabase>().pokemonDetailDao() }
    single { get<PokemonDatabase>().pokemonSpeciesDao() }
    single { get<PokemonDatabase>().evolutionChainDao() }
    single { get<PokemonDatabase>().typeDao() }
    single { get<PokemonDatabase>().pokemonFavoriteDao() }

    // ─── Repository ─────────────────────────────────────────────
    single<PokemonRepository> {
        PokemonRepositoryImpl(
            api = get(),
            pokemonListDao = get(),
            pokemonDetailDao = get(),
            pokemonSpeciesDao = get(),
            evolutionChainDao = get(),
            typeDao = get(),
            favoriteDao = get(),
        )
    }
}
