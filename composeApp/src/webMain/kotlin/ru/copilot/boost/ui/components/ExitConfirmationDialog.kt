package ru.copilot.boost.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ExitConfirmationDialog(
    visible: Boolean,
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    pendingItems: List<String> = emptyList(),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    val detailsText = if (pendingItems.isEmpty()) {
        message
    } else {
        val pendingItemsText = pendingItems.joinToString(separator = "\n") { "- $it" }
        "$message\n$pendingItemsText"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = detailsText) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        },
    )
}
