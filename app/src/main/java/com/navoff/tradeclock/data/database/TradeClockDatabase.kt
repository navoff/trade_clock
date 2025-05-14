package com.navoff.tradeclock.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.navoff.tradeclock.data.database.dao.ExchangeDao
import com.navoff.tradeclock.data.database.entity.ExchangeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId

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
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Populate the database with initial data when it's created
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateDatabase(database.exchangeDao())
                            }
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
            // Create a list of requested stock exchanges
            val exchanges = listOf(
                // Europe
                ExchangeEntity(
                    id = "lse",
                    name = "London Stock Exchange",
                    timezoneName = "Europe/London",
                    openingTimeHour = 8,
                    openingTimeMinute = 0,
                    closingTimeHour = 16,
                    closingTimeMinute = 30,
                    continent = "Europe"
                ),
                ExchangeEntity(
                    id = "seb",
                    name = "Swiss Electronic Bourse",
                    timezoneName = "Europe/Zurich",
                    openingTimeHour = 9,
                    openingTimeMinute = 0,
                    closingTimeHour = 17,
                    closingTimeMinute = 30,
                    continent = "Europe"
                ),

                // North America
                ExchangeEntity(
                    id = "bmv",
                    name = "Bolsa Mexicana de Valores",
                    timezoneName = "America/Mexico_City",
                    openingTimeHour = 8,
                    openingTimeMinute = 30,
                    closingTimeHour = 15,
                    closingTimeMinute = 0,
                    continent = "North America"
                )
            )

            // Insert the exchanges into the database
            exchangeDao.insertExchanges(exchanges)
        }
    }
}