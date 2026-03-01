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
import ru.copilot.boost.presentation.SetupModuleDefinition
import ru.copilot.boost.presentation.SetupModuleId

@Composable
fun SetupSelectionScreen(
    modules: List<SetupModuleDefinition>,
    onStartFlow: (Set<SetupModuleId>) -> Unit,
    onBackHome: () -> Unit,
) {
    var selectedModules by remember { mutableStateOf<Set<SetupModuleId>>(emptySet()) }
    val hasSelection = selectedModules.isNotEmpty()

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

        modules.forEachIndexed { index, module ->
            val isChecked = module.id in selectedModules
            SetupOptionRow(
                title = module.title,
                description = module.description,
                checked = isChecked,
                onCheckedChange = { checked ->
                    selectedModules = if (checked) {
                        selectedModules + module.id
                    } else {
                        selectedModules - module.id
                    }
                },
                modifier = Modifier.padding(top = if (index == 0) 20.dp else 12.dp),
            )
        }

        Button(
            onClick = {
                onStartFlow(selectedModules)
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
