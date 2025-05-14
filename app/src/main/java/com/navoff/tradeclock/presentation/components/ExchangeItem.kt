package com.navoff.tradeclock.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.navoff.tradeclock.domain.models.Exchange
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Composable function for displaying an exchange item in the list.
 */
@Composable
fun ExchangeItem(
    exchange: Exchange,
    modifier: Modifier = Modifier
) {
    val now = ZonedDateTime.now(exchange.timezone)
    val currentTime = LocalTime.of(now.hour, now.minute)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val isOpen = if (exchange.openingTime.isBefore(exchange.closingTime)) {
        currentTime.isAfter(exchange.openingTime) && currentTime.isBefore(exchange.closingTime)
    } else {
        currentTime.isAfter(exchange.openingTime) || currentTime.isBefore(exchange.closingTime)
    }

    val statusColor = if (isOpen) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exchange.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (isOpen) "OPEN" else "CLOSED",
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Continent: ${exchange.continent}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Local time: ${now.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Opens: ${exchange.openingTime.format(timeFormatter)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Closes: ${exchange.closingTime.format(timeFormatter)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}