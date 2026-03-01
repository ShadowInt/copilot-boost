package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    appVersion: String,
    onOpenTweaks: () -> Unit,
    onOpenBinds: () -> Unit,
    onOpenLaunchArgs: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("CopilotBoost", style = MaterialTheme.typography.headlineMedium)
        Text("Версия: $appVersion", modifier = Modifier.padding(top = 4.dp))

        ModuleCard(
            title = "Твики конфигурации",
            description = "Изменение параметров client.cfg с diff-просмотром",
            onOpen = onOpenTweaks,
            modifier = Modifier.padding(top = 20.dp),
        )
        ModuleCard(
            title = "Параметры запуска",
            description = "Настройка аргументов запуска клиента",
            onOpen = onOpenLaunchArgs,
            modifier = Modifier.padding(top = 12.dp),
        )
        ModuleCard(
            title = "Функционал биндов",
            description = "Настройка и управление биндами",
            onOpen = onOpenBinds,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}
