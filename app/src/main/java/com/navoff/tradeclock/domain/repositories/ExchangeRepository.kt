package com.navoff.tradeclock.domain.repositories

import com.navoff.tradeclock.domain.models.Exchange
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing exchange data.
 */
interface ExchangeRepository {
    /**
     * Get all exchanges as a Flow.
     * @return Flow of list of exchanges
     */
    fun getAllExchanges(): Flow<List<Exchange>>

    /**
     * Get selected exchanges as a Flow.
     * @return Flow of list of selected exchanges
     */
    fun getSelectedExchanges(): Flow<List<Exchange>>

    /**
     * Update exchange selection status.
     * @param exchangeId ID of the exchange to update
     * @param isSelected New selection status
     */
    suspend fun updateExchangeSelection(exchangeId: String, isSelected: Boolean)

    /**
     * Get an exchange by ID.
     * @param exchangeId ID of the exchange to retrieve
     * @return The exchange if found, null otherwise
     */
    suspend fun getExchangeById(exchangeId: String): Exchange?
}