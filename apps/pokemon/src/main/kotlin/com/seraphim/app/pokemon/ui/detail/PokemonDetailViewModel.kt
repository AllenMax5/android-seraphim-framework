package com.seraphim.app.pokemon.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seraphim.pokemon.shared.data.repository.PokemonRepository
import com.seraphim.pokemon.shared.domain.model.EvolutionChain
import com.seraphim.pokemon.shared.domain.model.PokemonDetail
import com.seraphim.pokemon.shared.domain.model.PokemonSpecies
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PokemonDetailViewModel(
    private val pokemonId: Int,
    private val repository: PokemonRepository,
) : ViewModel() {

    val detail: StateFlow<PokemonDetail?> = repository.getPokemonDetail(pokemonId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val species: StateFlow<PokemonSpecies?> = detail
        .filterNotNull()
        .flatMapLatest { detail ->
            val speciesId = detail.speciesId ?: detail.id
            repository.getPokemonSpecies(speciesId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val evolutionChain: StateFlow<EvolutionChain?> = species
        .filterNotNull()
        .flatMapLatest { species ->
            repository.getEvolutionChain(species.evolutionChainId ?: 0)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        viewModelScope.launch {
            repository.isFavorite(pokemonId).collect { _isFavorite.value = it }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            repository.toggleFavorite(pokemonId)
        }
    }
}
