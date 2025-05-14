package com.navoff.tradeclock

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the TradeClock app.
 * This class is responsible for initializing app-wide dependencies.
 */
@HiltAndroidApp
class TradeClockApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize ThreeTenABP for time zone handling
        AndroidThreeTen.init(this)
    }
}