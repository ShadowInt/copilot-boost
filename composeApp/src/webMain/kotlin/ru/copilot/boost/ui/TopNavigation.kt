package ru.copilot.boost.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.copilot.boost.navigation.AppScreen

@Composable
fun TopNavigation(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(onClick = { onNavigate(AppScreen.Home) }) { Text("Главная") }
        Button(onClick = { onNavigate(AppScreen.Tweaks) }) { Text("Твики") }
        Button(onClick = { onNavigate(AppScreen.Binds) }) { Text("Бинды") }
        Button(onClick = { onNavigate(AppScreen.LaunchArgs) }) { Text("Параметры запуска") }
    }
}
