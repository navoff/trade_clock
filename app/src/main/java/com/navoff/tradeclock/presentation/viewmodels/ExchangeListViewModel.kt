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
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
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
                    // Update exchanges with current time information
                    val updatedExchanges = updateExchangesWithTimeInfo(exchanges)

                    _uiState.value = _uiState.value.copy(
                        exchanges = updatedExchanges,
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
     * Update exchanges with current time information.
     * This calculates the current local time and open/closed status for each exchange.
     */
    private fun updateExchangesWithTimeInfo(exchanges: List<Exchange>): List<Exchange> {
        return exchanges.map { exchange ->
            // Calculate current local time at the exchange's location
            val now = ZonedDateTime.now(exchange.timezone)
            val localTime = LocalTime.of(now.hour, now.minute)

            // Calculate if the exchange is open
            val isOpen = if (exchange.openingTime.isBefore(exchange.closingTime)) {
                localTime.isAfter(exchange.openingTime) && localTime.isBefore(exchange.closingTime)
            } else {
                localTime.isAfter(exchange.openingTime) || localTime.isBefore(exchange.closingTime)
            }

            // Create a new Exchange object with updated time information
            // Preserve the expanded state when updating time information
            exchange.copy(
                currentLocalTime = localTime,
                isOpen = isOpen,
                isExpanded = exchange.isExpanded
            )
        }
    }

    /**
     * Update the current time and exchange statuses.
     * This is called by the MainActivity when the system time changes (every minute)
     * or when the app is resumed.
     */
    fun updateCurrentTime() {
        viewModelScope.launch {
            // Update the current time in the UI state
            val currentTime = LocalDateTime.now()

            if (_uiState.value.exchanges.isEmpty()) {
                // If no exchanges are loaded yet, load them
                _uiState.value = _uiState.value.copy(
                    currentLocalTime = currentTime
                )
                loadExchanges()
            } else {
                // Update the exchange list with current time information
                val updatedExchanges = updateExchangesWithTimeInfo(_uiState.value.exchanges)

                // Update the UI state with the new exchange list and current time
                _uiState.value = _uiState.value.copy(
                    currentLocalTime = currentTime,
                    exchanges = updatedExchanges
                )
            }
        }
    }

    /**
     * Toggle the selection status of an exchange.
     */
    fun toggleExchangeSelection(exchangeId: String) {
        // Implementation will be added later
    }

    /**
     * Toggle the expanded state of an exchange.
     * @param exchangeId The ID of the exchange to toggle
     */
    fun toggleExchangeExpanded(exchangeId: String) {
        viewModelScope.launch {
            val currentExchanges = _uiState.value.exchanges
            val updatedExchanges = currentExchanges.map { exchange ->
                if (exchange.id == exchangeId) {
                    // Toggle the expanded state for the selected exchange
                    exchange.copy(isExpanded = !exchange.isExpanded)
                } else {
                    // Optionally, collapse other exchanges when one is expanded
                    // exchange.copy(isExpanded = false)
                    exchange
                }
            }

            _uiState.value = _uiState.value.copy(
                exchanges = updatedExchanges
            )
        }
    }
}