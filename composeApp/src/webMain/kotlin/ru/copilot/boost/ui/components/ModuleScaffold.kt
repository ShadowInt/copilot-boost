package ru.copilot.boost.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ModuleScaffold(
    title: String,
    onBack: () -> Unit,
    primaryActionText: String? = null,
    primaryActionEnabled: Boolean = true,
    onPrimaryAction: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        BackNavigationBar(
            title = title,
            onBack = onBack,
            primaryActionText = primaryActionText,
            primaryActionEnabled = primaryActionEnabled,
            onPrimaryAction = onPrimaryAction,
        )
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
private fun BackNavigationBar(
    title: String,
    onBack: () -> Unit,
    primaryActionText: String? = null,
    primaryActionEnabled: Boolean = true,
    onPrimaryAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            Button(onClick = onBack) {
                Text("Назад")
            }
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            if (primaryActionText != null && onPrimaryAction != null) {
                Button(
                    onClick = onPrimaryAction,
                    enabled = primaryActionEnabled,
                ) {
                    Text(primaryActionText)
                }
            }
        }
    }
}
