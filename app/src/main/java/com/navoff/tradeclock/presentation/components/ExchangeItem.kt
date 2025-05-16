package com.navoff.tradeclock.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.navoff.tradeclock.domain.models.Exchange
import org.threeten.bp.format.DateTimeFormatter
import androidx.core.net.toUri

/**
 * Composable function for displaying an exchange item in the list.
 */
/**
 * Calculate the time remaining until the exchange opens or closes.
 * @param exchange The exchange to calculate time for
 * @return A formatted string representing the time remaining (e.g. "2h 30m")
 */
private fun calculateTimeRemaining(exchange: Exchange): String {
    val currentTime = exchange.currentLocalTime

    return if (exchange.isOpen) {
        // Exchange is open, calculate time until closing
        val closingTime = exchange.closingTime

        // Handle case where closing time is on the next day
        val minutesUntilClosing = if (closingTime.isBefore(exchange.openingTime)) {
            // Closing time is on the next day
            val minutesToMidnight = (24 * 60) - (currentTime.hour * 60 + currentTime.minute)
            minutesToMidnight + (closingTime.hour * 60 + closingTime.minute)
        } else {
            // Closing time is on the same day
            (closingTime.hour * 60 + closingTime.minute) - (currentTime.hour * 60 + currentTime.minute)
        }

        formatMinutesToTimeString(minutesUntilClosing)
    } else {
        // Exchange is closed, calculate time until opening
        val openingTime = exchange.openingTime

        // Handle case where we're after closing time but before midnight
        val minutesUntilOpening = if (currentTime.isAfter(exchange.closingTime) && exchange.closingTime.isAfter(exchange.openingTime)) {
            // We're after closing time but before midnight, and opening time is on the next day
            val minutesToMidnight = (24 * 60) - (currentTime.hour * 60 + currentTime.minute)
            minutesToMidnight + (openingTime.hour * 60 + openingTime.minute)
        } else if (currentTime.isBefore(openingTime)) {
            // We're before opening time on the same day
            (openingTime.hour * 60 + openingTime.minute) - (currentTime.hour * 60 + currentTime.minute)
        } else {
            // We're after closing time and opening time is on the next day
            val minutesToMidnight = (24 * 60) - (currentTime.hour * 60 + currentTime.minute)
            minutesToMidnight + (openingTime.hour * 60 + openingTime.minute)
        }

        formatMinutesToTimeString(minutesUntilOpening)
    }
}

/**
 * Format minutes to a readable time string (e.g. "2h 30m" or "45m")
 * @param totalMinutes The total minutes to format
 * @return A formatted string
 */
private fun formatMinutesToTimeString(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        else -> "${minutes}m"
    }
}

@Composable
fun ExchangeItem(
    exchange: Exchange,
    onToggleExpanded: (String) -> Unit,
    onToggleSelection: (String) -> Unit = {},
    isEditMode: Boolean = false,
    isDragging: Boolean = false,
    modifier: Modifier = Modifier
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val context = LocalContext.current

    // Animate elevation when dragging
    val elevation = animateFloatAsState(
        targetValue = if (isDragging) 8f else 4f,
        label = "Card Elevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .clickable {
                if (!isEditMode) {
                    onToggleExpanded(exchange.id)
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.value.dp)
    ) {
        // Go back to Row but with a different structure
        Row(
            modifier = Modifier.padding(
                end = 16.dp,
                top = 12.dp,
                bottom = 12.dp
            )
        ) {
            // Show checkbox in edit mode in its own column
            if (isEditMode) {
                Checkbox(
                    checked = exchange.isSelected,
                    onCheckedChange = { onToggleSelection(exchange.id) },
                    modifier = Modifier.padding(top = 0.dp)
                )
            }

            // Main content column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = if (isEditMode) 0.dp else 16.dp
                    )
            ) {

                // Header row with exchange name and local time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = exchange.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = exchange.currentLocalTime.format(timeFormatter),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // Fixed distance
                    Text(
                        text = if (exchange.isOpen) "🟢" else "🔴",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Basic information row with country and trading hours
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${exchange.flag} ${exchange.country}, ${exchange.city}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${exchange.openingTime.format(timeFormatter)} - ${
                            exchange.closingTime.format(
                                timeFormatter
                            )
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (exchange.isExpanded && !isEditMode) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = if (exchange.isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Expanded content - only show when not in edit mode and exchange is expanded
                AnimatedVisibility(
                    visible = exchange.isExpanded && !isEditMode,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        // Status information with time remaining
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Status:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            // Calculate time remaining until opening/closing
                            val timeRemaining = calculateTimeRemaining(exchange)

                            Text(
                                text = if (exchange.isOpen) {
                                    "Open (closes in $timeRemaining)"
                                } else {
                                    "Closed (opens in $timeRemaining)"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (exchange.isOpen)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.error
                            )
                        }

                        // Button to view schedule
                        TextButton(
                            onClick = {
                                // Check if scheduleUrl is not empty
                                if (exchange.scheduleUrl.isNotEmpty()) {
                                    // Open the exchange's schedule URL in the default browser
                                    val intent =
                                        Intent(Intent.ACTION_VIEW, exchange.scheduleUrl.toUri())
                                    context.startActivity(intent)
                                } else {
                                    // Fallback URL if scheduleUrl is empty
                                    val fallbackUrl = "https://www.google.com/search?q=${
                                        exchange.name.replace(
                                            " ",
                                            "+"
                                        )
                                    }+trading+schedule"
                                    val intent = Intent(Intent.ACTION_VIEW, fallbackUrl.toUri())
                                    context.startActivity(intent)
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("View Schedule")
                        }
                    }
                }
            }
        }
    }
}