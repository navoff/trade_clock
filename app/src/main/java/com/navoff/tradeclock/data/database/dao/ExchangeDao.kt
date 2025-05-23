package com.navoff.tradeclock.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.navoff.tradeclock.data.database.entity.ExchangeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the exchanges table.
 */
@Dao
interface ExchangeDao {
    /**
     * Get all exchanges as a Flow.
     */
    @Query("SELECT * FROM exchanges ORDER BY displayOrder, continent, name")
    fun getAllExchanges(): Flow<List<ExchangeEntity>>

    /**
     * Get selected exchanges as a Flow.
     */
    @Query("SELECT * FROM exchanges WHERE isSelected = 1 ORDER BY displayOrder, continent, name")
    fun getSelectedExchanges(): Flow<List<ExchangeEntity>>

    /**
     * Get an exchange by ID.
     */
    @Query("SELECT * FROM exchanges WHERE id = :exchangeId")
    suspend fun getExchangeById(exchangeId: String): ExchangeEntity?

    /**
     * Insert exchanges into the database.
     * If an exchange with the same ID already exists, it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchanges(exchanges: List<ExchangeEntity>)

    /**
     * Update an exchange in the database.
     */
    @Update
    suspend fun updateExchange(exchange: ExchangeEntity)

    /**
     * Update the selection status of an exchange.
     */
    @Query("UPDATE exchanges SET isSelected = :isSelected WHERE id = :exchangeId")
    suspend fun updateExchangeSelection(exchangeId: String, isSelected: Boolean)

    /**
     * Update the display order of an exchange.
     */
    @Query("UPDATE exchanges SET displayOrder = :displayOrder WHERE id = :exchangeId")
    suspend fun updateExchangeDisplayOrder(exchangeId: String, displayOrder: Int)

    /**
     * Update the display order of multiple exchanges in a single transaction.
     * This is used when reordering exchanges with drag and drop.
     */
    @Transaction
    suspend fun updateExchangeDisplayOrders(updates: Map<String, Int>) {
        updates.forEach { (exchangeId, displayOrder) ->
            updateExchangeDisplayOrder(exchangeId, displayOrder)
        }
    }
}