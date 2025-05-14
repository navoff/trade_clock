package com.navoff.tradeclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.navoff.tradeclock.presentation.screens.ExchangeListScreen
import com.navoff.tradeclock.ui.theme.TradeClockTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the TradeClock application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TradeClockTheme {
                ExchangeListScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}