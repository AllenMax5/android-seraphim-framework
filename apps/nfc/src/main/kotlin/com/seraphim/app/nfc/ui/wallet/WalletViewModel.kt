package com.seraphim.app.nfc.ui.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seraphim.nfc.shared.data.repository.HillCardRepository
import com.seraphim.nfc.shared.domain.model.HillCardInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WalletViewModel(
    private val repository: HillCardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    private val _lastReadResult = MutableStateFlow<ReadResult?>(null)
    val lastReadResult: StateFlow<ReadResult?> = _lastReadResult.asStateFlow()

    init {
        loadCards()
    }

    private fun loadCards() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val cards = repository.getAllCards()
                _uiState.update {
                    it.copy(cards = cards, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _lastReadResult.value = ReadResult.Error("加载失败: ${e.message}")
            }
        }
    }

    fun selectGroup(group: String) {
        _uiState.update { it.copy(selectedGroup = group) }
    }

    fun search(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun deleteCard(cardId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCard(cardId)
                val updated = _uiState.value.cards.filter { it.id != cardId }
                _uiState.update { it.copy(cards = updated) }
                _lastReadResult.value = ReadResult.Success("卡片已删除")
            } catch (e: Exception) {
                _lastReadResult.value = ReadResult.Error("删除失败: ${e.message}")
            }
        }
    }

    fun saveCard(card: HillCardInfo) {
        viewModelScope.launch {
            try {
                repository.insertCard(card)
                val updated = _uiState.value.cards + card
                _uiState.update { it.copy(cards = updated) }
                _lastReadResult.value = ReadResult.Success("已保存: ${card.name} (${card.uid})")
            } catch (e: Exception) {
                _lastReadResult.value = ReadResult.Error("保存失败: ${e.message}")
            }
        }
    }

    fun clearReadResult() {
        _lastReadResult.value = null
    }

    fun refresh() {
        loadCards()
    }
}

sealed class ReadResult {
    data class Success(val message: String) : ReadResult()
    data class Error(val message: String) : ReadResult()
}
