package com.navoff.tradeclock.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navoff.tradeclock.domain.models.Exchange
import com.navoff.tradeclock.domain.usecases.GetExchangesWithStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import javax.inject.Inject

/**
 * ViewModel for the exchange list screen.
 */
@HiltViewModel
class ExchangeListViewModel @Inject constructor(
    private val getExchangesWithStatusUseCase: GetExchangesWithStatusUseCase
) : ViewModel() {

    /**
     * UI state for the exchange list screen.
     */
    data class ExchangeListUiState(
        val exchanges: List<Exchange> = emptyList(),
        val currentLocalTime: LocalDateTime = LocalDateTime.now(),
        val isLoading: Boolean = true,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(ExchangeListUiState())
    val uiState: StateFlow<ExchangeListUiState> = _uiState

    init {
        loadExchanges()
        updateCurrentTime()
    }

    /**
     * Load exchanges from the repository.
     */
    private fun loadExchanges() {
        viewModelScope.launch {
            try {
                getExchangesWithStatusUseCase().stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                ).collect { exchanges ->
                    _uiState.value = _uiState.value.copy(
                        exchanges = exchanges,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Update the current time.
     * This should be called periodically to keep the time up to date.
     */
    private fun updateCurrentTime() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                currentLocalTime = LocalDateTime.now()
            )
        }
    }

    /**
     * Toggle the selection status of an exchange.
     */
    fun toggleExchangeSelection(exchangeId: String) {
        // Implementation will be added later
    }
}