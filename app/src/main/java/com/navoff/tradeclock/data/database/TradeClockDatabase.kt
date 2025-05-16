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
    version = 2, // Increased version number
    exportSchema = false
)
abstract class TradeClockDatabase : RoomDatabase() {

    abstract fun exchangeDao(): ExchangeDao

    companion object {
        @Volatile
        private var INSTANCE: TradeClockDatabase? = null

        /**
         * Migration from database version 1 to 2.
         * Adds the displayOrder column to the exchanges table and initializes it based on the current order.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the displayOrder column with a default value of 0
                database.execSQL("ALTER TABLE exchanges ADD COLUMN displayOrder INTEGER NOT NULL DEFAULT 0")

                // Initialize displayOrder for each exchange based on ID to ensure a stable order
                // This is a simpler approach that avoids complex subqueries
                val cursor = database.query("SELECT id FROM exchanges ORDER BY continent, name")
                var order = 0

                cursor.use {
                    while (cursor.moveToNext()) {
                        val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                        database.execSQL("UPDATE exchanges SET displayOrder = $order WHERE id = '$id'")
                        order++
                    }
                }
            }
        }

        fun getDatabase(context: Context): TradeClockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TradeClockDatabase::class.java,
                    "tradeclock_database"
                )
                .addMigrations(MIGRATION_1_2) // Add the migration
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
                    scheduleUrl = "https://www.google.com/search?q=London+Stock+Exchange+trading+schedule"
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
                    scheduleUrl = "https://www.nasdaq.com/market-activity/stock-market-holiday-schedule"
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
                    scheduleUrl = "https://www.jpx.co.jp/english/equities/trading/domestic/01.html"
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
                    scheduleUrl = "https://www.six-group.com/en/products-services/the-swiss-stock-exchange/trading/trading-provisions/trading-hours.html#scrollTo=trading-hours-overview"
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
                    scheduleUrl = "https://www.bmv.com.mx/en/Grupo_BMV/Calendario_de_dias_festivos/_rid/662/_mod/TAB_HORARIOS_NEG"
                )
            )

            // Insert the exchanges into the database
            exchangeDao.insertExchanges(exchanges)
        }
    }
}