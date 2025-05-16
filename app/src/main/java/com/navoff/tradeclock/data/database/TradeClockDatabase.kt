package com.navoff.tradeclock.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.navoff.tradeclock.data.database.dao.ExchangeDao
import com.navoff.tradeclock.data.database.entity.ExchangeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room database for the TradeClock application.
 */
@Database(
    entities = [ExchangeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TradeClockDatabase : RoomDatabase() {

    abstract fun exchangeDao(): ExchangeDao

    companion object {
        @Volatile
        private var INSTANCE: TradeClockDatabase? = null

        fun getDatabase(context: Context): TradeClockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TradeClockDatabase::class.java,
                    "tradeclock_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Populate the database in a background thread
                        CoroutineScope(Dispatchers.IO).launch {
                            populateDatabase(INSTANCE!!.exchangeDao())
                        }
                    }
                })
                .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * Populate the database with initial exchange data.
         */
        private suspend fun populateDatabase(exchangeDao: ExchangeDao) {
            // Create a list of stock exchanges with complete information
            val exchanges = listOf(
                ExchangeEntity(
                    id = "lse",
                    name = "London Stock Exchange",
                    timezoneName = "Europe/London",
                    openingTimeHour = 8,
                    openingTimeMinute = 0,
                    closingTimeHour = 16,
                    closingTimeMinute = 30,
                    continent = "Europe",
                    country = "UK",
                    city = "London",
                    flag = "🇬🇧",
                    scheduleUrl = "https://www.tradinghours.com/markets/lse"
                ),

                ExchangeEntity(
                    id = "nyse",
                    name = "New York Stock Exchange",
                    timezoneName = "America/New_York",
                    openingTimeHour = 9,
                    openingTimeMinute = 30,
                    closingTimeHour = 16,
                    closingTimeMinute = 0,
                    continent = "North America",
                    country = "USA",
                    city = "New York",
                    flag = "🇺🇸",
                    scheduleUrl = "https://www.tradinghours.com/markets/nyse"
                ),

                ExchangeEntity(
                    id = "tse",
                    name = "Tokyo Stock Exchange",
                    timezoneName = "Asia/Tokyo",
                    openingTimeHour = 9,
                    openingTimeMinute = 0,
                    closingTimeHour = 15,
                    closingTimeMinute = 30,
                    continent = "Asia",
                    country = "Japan",
                    city = "Tokyo",
                    flag = "🇯🇵",
                    scheduleUrl = "https://www.tradinghours.com/markets/tse"
                ),

                ExchangeEntity(
                    id = "seb",
                    name = "Swiss Electronic Bourse",
                    timezoneName = "Europe/Zurich",
                    openingTimeHour = 9,
                    openingTimeMinute = 0,
                    closingTimeHour = 17,
                    closingTimeMinute = 30,
                    continent = "Europe",
                    country = "Switzerland",
                    city = "Zurich",
                    flag = "🇨🇭",
                    scheduleUrl = "https://www.tradinghours.com/markets/seb"
                ),

                ExchangeEntity(
                    id = "bmv",
                    name = "Bolsa Mexicana de Valores",
                    timezoneName = "America/Mexico_City",
                    openingTimeHour = 7,
                    openingTimeMinute = 30,
                    closingTimeHour = 14,
                    closingTimeMinute = 0,
                    continent = "North America",
                    country = "Mexico",
                    city = "Mexico City",
                    flag = "🇲🇽",
                    scheduleUrl = "https://www.tradinghours.com/markets/bmv"
                ),

                ExchangeEntity(
                    id = "moex",
                    name = "Moscow Exchange",
                    timezoneName = "Europe/Moscow",
                    openingTimeHour = 9,
                    openingTimeMinute = 50,
                    closingTimeHour = 18,
                    closingTimeMinute = 50,
                    continent = "Europe",
                    country = "Russia",
                    city = "Moscow",
                    flag = "🇷🇺",
                    scheduleUrl = "https://www.tradinghours.com/markets/moex"
                ),

                ExchangeEntity(
                    id = "hkex",
                    name = "Hong Kong Stock Exchange",
                    timezoneName = "Asia/Hong_Kong",
                    openingTimeHour = 9,
                    openingTimeMinute = 30,
                    closingTimeHour = 16,
                    closingTimeMinute = 0,
                    continent = "Asia",
                    country = "Hong Kong",
                    city = "Hong Kong",
                    flag = "🇭🇰",
                    scheduleUrl = "https://www.tradinghours.com/markets/hkex"
                ),

                ExchangeEntity(
                    id = "nasdaq",
                    name = "NASDAQ",
                    timezoneName = "America/New_York",
                    openingTimeHour = 9,
                    openingTimeMinute = 30,
                    closingTimeHour = 16,
                    closingTimeMinute = 0,
                    continent = "North America",
                    country = "USA",
                    city = "New York",
                    flag = "🇺🇸",
                    scheduleUrl = "https://www.tradinghours.com/markets/nasdaq"
                )
            )

            // Insert the exchanges into the database
            exchangeDao.insertExchanges(exchanges)
        }
    }
}