package com.navoff.tradeclock.di

import com.navoff.tradeclock.data.repositories.ExchangeRepositoryImpl
import com.navoff.tradeclock.domain.repositories.ExchangeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides repository dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the ExchangeRepositoryImpl to the ExchangeRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindExchangeRepository(
        exchangeRepositoryImpl: ExchangeRepositoryImpl
    ): ExchangeRepository
}