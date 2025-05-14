package com.navoff.tradeclock.data.repositories

import com.navoff.tradeclock.data.database.dao.ExchangeDao
import com.navoff.tradeclock.data.database.entity.ExchangeEntity
import com.navoff.tradeclock.domain.models.Exchange
import com.navoff.tradeclock.domain.repositories.ExchangeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the ExchangeRepository interface.
 * This class is responsible for providing access to exchange data from the database.
 */
@Singleton
class ExchangeRepositoryImpl @Inject constructor(
    private val exchangeDao: ExchangeDao
) : ExchangeRepository {

    override fun getAllExchanges(): Flow<List<Exchange>> {
        return exchangeDao.getAllExchanges().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSelectedExchanges(): Flow<List<Exchange>> {
        return exchangeDao.getSelectedExchanges().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun updateExchangeSelection(exchangeId: String, isSelected: Boolean) {
        exchangeDao.updateExchangeSelection(exchangeId, isSelected)
    }

    override suspend fun getExchangeById(exchangeId: String): Exchange? {
        return exchangeDao.getExchangeById(exchangeId)?.toDomainModel()
    }
}