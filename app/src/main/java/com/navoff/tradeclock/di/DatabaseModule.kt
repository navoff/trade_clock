package com.navoff.tradeclock.di

import android.content.Context
import com.navoff.tradeclock.data.database.TradeClockDatabase
import com.navoff.tradeclock.data.database.dao.ExchangeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the Room database instance.
     */
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TradeClockDatabase {
        return TradeClockDatabase.getDatabase(context)
    }

    /**
     * Provides the ExchangeDao.
     */
    @Provides
    fun provideExchangeDao(database: TradeClockDatabase): ExchangeDao {
        return database.exchangeDao()
    }
}