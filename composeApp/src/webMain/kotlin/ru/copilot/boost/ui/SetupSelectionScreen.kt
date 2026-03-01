package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SetupSelectionScreen(
    onStartFlow: (
        includeTweaks: Boolean,
        includeLaunchArgs: Boolean,
        includeBinds: Boolean,
    ) -> Unit,
    onBackHome: () -> Unit,
) {
    var tweaksSelected by remember { mutableStateOf(false) }
    var launchArgsSelected by remember { mutableStateOf(false) }
    var bindsSelected by remember { mutableStateOf(false) }
    val hasSelection = tweaksSelected || launchArgsSelected || bindsSelected

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Что вы хотите настроить?", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Выберите один или несколько разделов и нажмите «Продолжить»",
            modifier = Modifier.padding(top = 8.dp),
        )

        SetupOptionRow(
            title = "Твики",
            description = "Понадобится файл client.cfg через drag-and-drop",
            checked = tweaksSelected,
            onCheckedChange = { tweaksSelected = it },
            modifier = Modifier.padding(top = 20.dp),
        )
        SetupOptionRow(
            title = "Параметры запуска",
            description = "Настройка аргументов запуска клиента",
            checked = launchArgsSelected,
            onCheckedChange = { launchArgsSelected = it },
            modifier = Modifier.padding(top = 12.dp),
        )
        SetupOptionRow(
            title = "Бинды",
            description = "Настройка и управление биндами",
            checked = bindsSelected,
            onCheckedChange = { bindsSelected = it },
            modifier = Modifier.padding(top = 12.dp),
        )

        Button(
            onClick = {
                onStartFlow(tweaksSelected, launchArgsSelected, bindsSelected)
            },
            enabled = hasSelection,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text("Начать")
        }

        Button(
            onClick = onBackHome,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text("На главную")
        }
    }
}

@Composable
private fun SetupOptionRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Column(modifier = Modifier.padding(top = 10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
