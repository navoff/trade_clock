package com.navoff.tradeclock.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
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
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                try {
                    // Check if scheduleUrl is not empty
                    if (exchange.scheduleUrl.isNotEmpty()) {
                        // Open the exchange's schedule URL in the default browser
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exchange.scheduleUrl))
                        context.startActivity(intent)
                    } else {
                        // Fallback URL if scheduleUrl is empty
                        val fallbackUrl = "https://www.google.com/search?q=${exchange.name.replace(" ", "+")}+trading+schedule"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
                        context.startActivity(intent)
                    }
                } catch (e: Exception) {
                    // If there's any error, use a fallback URL
                    val fallbackUrl = "https://www.google.com/search?q=${exchange.name.replace(" ", "+")}+trading+schedule"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
                    context.startActivity(intent)
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (exchange.isOpen) "🟢 " else "🔴 ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
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
            }

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
                    text = "${exchange.openingTime.format(timeFormatter)} - ${exchange.closingTime.format(timeFormatter)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}