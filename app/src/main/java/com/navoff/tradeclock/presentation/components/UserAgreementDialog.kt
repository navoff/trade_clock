package com.navoff.tradeclock.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Dialog that displays the user agreement and requires acceptance to continue.
 *
 * @param onAccept Callback when the user accepts the agreement
 */
@Composable
fun UserAgreementDialog(
    onAccept: () -> Unit
) {
    var isAgreementAccepted by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Using a custom Dialog instead of AlertDialog to have more control over padding
    Dialog(
        onDismissRequest = { /* Cannot dismiss without accepting */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceBright,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title
                Text(
                    text = "User Agreement",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Scrollable agreement text
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "This app shows you stock exchange hours to make your life easier 🎉, " +
                                "but we can't guarantee it's always 100% accurate.\n\n" +
                                "Just so you know, we don't account for holidays or daily trading breaks " +
                                "since these can vary and change 😢.\n\n" +
                                "Please don't make important trading decisions based only on what you see here❗",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.height(16.dp))
                // Checkbox row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAgreementAccepted,
                        onCheckedChange = { isAgreementAccepted = it }
                    )
                    Text(
                        text = "I have read and accept the agreement",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Small space between checkbox and button
                Spacer(modifier = Modifier.height(8.dp))

                // Accept button
                Button(
                    onClick = onAccept,
                    enabled = isAgreementAccepted,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Accept",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}