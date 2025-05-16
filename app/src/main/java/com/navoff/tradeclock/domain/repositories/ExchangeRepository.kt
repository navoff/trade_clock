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

    /**
     * Update the display order of an exchange.
     * @param exchangeId ID of the exchange to update
     * @param displayOrder New display order value
     */
    suspend fun updateExchangeDisplayOrder(exchangeId: String, displayOrder: Int)

    /**
     * Update the display order of multiple exchanges in a single transaction.
     * This is used when reordering exchanges with drag and drop.
     * @param updates Map of exchange IDs to their new display order values
     */
    suspend fun updateExchangeDisplayOrders(updates: Map<String, Int>)
}