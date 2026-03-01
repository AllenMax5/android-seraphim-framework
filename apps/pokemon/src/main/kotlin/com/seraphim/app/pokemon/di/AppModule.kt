package com.seraphim.app.pokemon.di

import com.seraphim.app.pokemon.ui.detail.PokemonDetailViewModel
import com.seraphim.app.pokemon.ui.favorites.FavoritesViewModel
import com.seraphim.app.pokemon.ui.home.HomeViewModel
import com.seraphim.app.pokemon.ui.types.TypesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { params -> PokemonDetailViewModel(params.get(), get()) }
    viewModel { TypesViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
}
