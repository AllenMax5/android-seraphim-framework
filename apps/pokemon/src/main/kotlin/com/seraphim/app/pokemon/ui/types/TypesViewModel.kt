package com.seraphim.app.pokemon.ui.types

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seraphim.pokemon.shared.data.repository.PokemonRepository
import com.seraphim.pokemon.shared.domain.model.TypeDetail
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TypesViewModel(
    repository: PokemonRepository,
) : ViewModel() {

    val types: StateFlow<List<TypeDetail>> = repository.getAllTypes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
