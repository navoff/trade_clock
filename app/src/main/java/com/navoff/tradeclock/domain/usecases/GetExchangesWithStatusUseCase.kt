package com.navoff.tradeclock.domain.usecases

import com.navoff.tradeclock.domain.models.Exchange
import com.navoff.tradeclock.domain.repositories.ExchangeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for getting exchanges.
 */
class GetExchangesWithStatusUseCase @Inject constructor(
    private val exchangeRepository: ExchangeRepository
) {
    /**
     * Get selected exchanges.
     * @return Flow of list of exchanges
     */
    operator fun invoke(): Flow<List<Exchange>> {
        return exchangeRepository.getSelectedExchanges()
    }
}