package com.seraphim.app.pokemon.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seraphim.pokemon.shared.data.repository.PokemonRepository
import com.seraphim.pokemon.shared.domain.model.PokemonListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: PokemonRepository,
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private val _offset = MutableStateFlow(0)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val pokemonList: StateFlow<List<PokemonListItem>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            repository.getPokemonList(offset = 0, limit = _offset.value + PAGE_SIZE)
        } else {
            repository.searchPokemon(query)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun loadMore() {
        viewModelScope.launch {
            _offset.value += PAGE_SIZE
            repository.refreshPokemonList(offset = _offset.value, limit = PAGE_SIZE)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshPokemonList(offset = 0, limit = _offset.value + PAGE_SIZE)
            _isRefreshing.value = false
        }
    }

    fun toggleFavorite(pokemonId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(pokemonId)
        }
    }
}
