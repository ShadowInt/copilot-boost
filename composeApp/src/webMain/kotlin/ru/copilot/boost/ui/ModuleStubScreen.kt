package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ModuleStubScreen(
    title: String,
    description: String,
    onBack: (() -> Unit)? = null,
    backButtonText: String = stringResource(Res.string.nav_back),
    onPrimaryAction: (() -> Unit)? = null,
    primaryActionText: String = stringResource(Res.string.flow_action_next),
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text(description, modifier = Modifier.padding(top = 8.dp))
        if (onPrimaryAction != null) {
            Button(onClick = onPrimaryAction, modifier = Modifier.padding(top = 16.dp)) {
                Text(primaryActionText)
            }
        }
        if (onBack != null) {
            Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                Text(backButtonText)
            }
        }
    }
}
