package ru.copilot.boost.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ModuleCard(
    title: String,
    description: String,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray)
            .padding(16.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(description, modifier = Modifier.padding(top = 4.dp))
        Button(onClick = onOpen, modifier = Modifier.padding(top = 10.dp)) {
            Text("Открыть")
        }
    }
}
