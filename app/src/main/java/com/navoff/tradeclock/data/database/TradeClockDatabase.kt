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
    version = 2,
    exportSchema = false
)
abstract class TradeClockDatabase : RoomDatabase() {

    abstract fun exchangeDao(): ExchangeDao

    companion object {
        @Volatile
        private var INSTANCE: TradeClockDatabase? = null

        // Migration from version 1 to 2: Add weekday columns
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add weekday columns with default values
                database.execSQL("ALTER TABLE exchanges ADD COLUMN isOpenMonday INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE exchanges ADD COLUMN isOpenTuesday INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE exchanges ADD COLUMN isOpenWednesday INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE exchanges ADD COLUMN isOpenThursday INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE exchanges ADD COLUMN isOpenFriday INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE exchanges ADD COLUMN isOpenSaturday INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE exchanges ADD COLUMN isOpenSunday INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): TradeClockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TradeClockDatabase::class.java,
                    "tradeclock_database"
                )
                .addMigrations(MIGRATION_1_2)
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
                    scheduleUrl = "https://www.tradinghours.com/markets/lse",
                    isOpenMonday = true,
                    isOpenTuesday = true,
                    isOpenWednesday = true,
                    isOpenThursday = true,
                    isOpenFriday = true,
                    isOpenSaturday = false,
                    isOpenSunday = false
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
                    scheduleUrl = "https://www.tradinghours.com/markets/nyse",
                    isOpenMonday = true,
                    isOpenTuesday = true,
                    isOpenWednesday = true,
                    isOpenThursday = true,
                    isOpenFriday = true,
                    isOpenSaturday = false,
                    isOpenSunday = false
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
                    scheduleUrl = "https://www.tradinghours.com/markets/tse",
                    isOpenMonday = true,
                    isOpenTuesday = true,
                    isOpenWednesday = true,
                    isOpenThursday = true,
                    isOpenFriday = true,
                    isOpenSaturday = false,
                    isOpenSunday = false
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
                    scheduleUrl = "https://www.tradinghours.com/markets/seb",
                    isOpenMonday = true,
                    isOpenTuesday = true,
                    isOpenWednesday = true,
                    isOpenThursday = true,
                    isOpenFriday = true,
                    isOpenSaturday = false,
                    isOpenSunday = false
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
                    scheduleUrl = "https://www.tradinghours.com/markets/bmv",
                    isOpenMonday = true,
                    isOpenTuesday = true,
                    isOpenWednesday = true,
                    isOpenThursday = true,
                    isOpenFriday = true,
                    isOpenSaturday = false,
                    isOpenSunday = false
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
                    scheduleUrl = "https://www.tradinghours.com/markets/moex",
                    isOpenMonday = true,
                    isOpenTuesday = true,
                    isOpenWednesday = true,
                    isOpenThursday = true,
                    isOpenFriday = true,
                    isOpenSaturday = false,
                    isOpenSunday = false
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
                    scheduleUrl = "https://www.tradinghours.com/markets/hkex",
                    isOpenMonday = true,
                    isOpenTuesday = true,
                    isOpenWednesday = true,
                    isOpenThursday = true,
                    isOpenFriday = true,
                    isOpenSaturday = false,
                    isOpenSunday = false
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
                    scheduleUrl = "https://www.tradinghours.com/markets/nasdaq",
                    isOpenMonday = true,
                    isOpenTuesday = true,
                    isOpenWednesday = true,
                    isOpenThursday = true,
                    isOpenFriday = true,
                    isOpenSaturday = false,
                    isOpenSunday = false
                )
            )

            // Insert the exchanges into the database
            exchangeDao.insertExchanges(exchanges)
        }
    }
}