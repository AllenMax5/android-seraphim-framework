package com.seraphim.app.pokemon.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seraphim.pokemon.shared.data.repository.PokemonRepository
import com.seraphim.pokemon.shared.domain.model.PokemonListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(
    private val repository: PokemonRepository,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val favorites: StateFlow<List<PokemonListItem>> = repository.getFavoriteIds()
        .flatMapLatest { ids ->
            if (ids.isEmpty()) {
                flowOf(emptyList())
            } else {
                // 从本地列表缓存获取收藏的宝可梦
                repository.getPokemonList(offset = 0, limit = 1500)
                    .map { list -> list.filter { it.id in ids } }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
