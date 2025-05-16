package com.navoff.tradeclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.navoff.tradeclock.presentation.components.UserAgreementDialog
import com.navoff.tradeclock.presentation.screens.ExchangeListScreen
import com.navoff.tradeclock.presentation.viewmodels.ExchangeListViewModel
import com.navoff.tradeclock.presentation.viewmodels.UserAgreementViewModel
import com.navoff.tradeclock.ui.theme.TradeClockTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the TradeClock application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val exchangeViewModel: ExchangeListViewModel by viewModels()
    private val agreementViewModel: UserAgreementViewModel by viewModels()

    // BroadcastReceiver to listen for time tick events (sent every minute)
    private val timeTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_TIME_TICK) {
                // Update time in ViewModel when system time changes
                exchangeViewModel.updateCurrentTime()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TradeClockTheme {
                // Collect the agreement state
                val agreementState by agreementViewModel.uiState.collectAsState()

                // Show the main screen
                ExchangeListScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = exchangeViewModel
                )

                // Show the agreement dialog if the user hasn't accepted it yet
                if (!agreementState.isLoading && !agreementState.hasAcceptedAgreement) {
                    UserAgreementDialog(
                        onAccept = {
                            agreementViewModel.acceptAgreement()
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Register receiver for time tick events
        registerReceiver(timeTickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

        // Update time immediately when app is resumed
        exchangeViewModel.updateCurrentTime()
    }

    override fun onPause() {
        super.onPause()

        // Unregister receiver when app is paused
        try {
            unregisterReceiver(timeTickReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver not registered
        }
    }
}