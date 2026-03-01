package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import ru.copilot.boost.domain.model.DiffRow
import ru.copilot.boost.domain.model.DiffRowType
import ru.copilot.boost.presentation.model.GraphicsPreset
import ru.copilot.boost.presentation.model.GraphicsUiState
import ru.copilot.boost.ui.components.FileDropZone

private const val CONFIG_FILE_NAME = "client.cfg"

@Composable
fun GraphicsScreen(
    state: GraphicsUiState,
    onPickFileClick: () -> Unit,
    onPresetSelected: (GraphicsPreset) -> Unit,
    onShadowQualityChanged: (Int) -> Unit,
    onTextureQualityChanged: (Int) -> Unit,
    onLightingQualityChanged: (Int) -> Unit,
    onTreeQualityChanged: (Int) -> Unit,
    onWaterReflectionsChanged: (Int) -> Unit,
    onGrassQualityChanged: (Int) -> Unit,
    onCloudQualityChanged: (Int) -> Unit,
    onAntialiasingChanged: (Int) -> Unit,
    onDownloadClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        if (!state.hasFile) {
            FileDropZone(
                isDragging = state.isDragging,
                idleMessage = "Перетащите $CONFIG_FILE_NAME файл в эту область",
                dragMessage = "Отпустите файл здесь",
                pickButtonText = "Выберите файл",
                onPickFileClick = onPickFileClick,
                errorMessage = state.uploadError,
            )
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            PresetSelector(
                selectedPreset = state.selectedPreset,
                onPresetSelected = onPresetSelected,
            )
            Spacer(modifier = Modifier.height(16.dp))
            GraphicsSliders(
                state = state,
                onShadowQualityChanged = onShadowQualityChanged,
                onTextureQualityChanged = onTextureQualityChanged,
                onLightingQualityChanged = onLightingQualityChanged,
                onTreeQualityChanged = onTreeQualityChanged,
                onWaterReflectionsChanged = onWaterReflectionsChanged,
                onGrassQualityChanged = onGrassQualityChanged,
                onCloudQualityChanged = onCloudQualityChanged,
                onAntialiasingChanged = onAntialiasingChanged,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDownloadClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text("Скачать $CONFIG_FILE_NAME")
            }
            if (state.showDiff) {
                Spacer(modifier = Modifier.height(24.dp))
                GraphicsDiffSection(diffRows = state.diffRows)
            }
        }
    }
}

@Composable
private fun PresetSelector(
    selectedPreset: GraphicsPreset,
    onPresetSelected: (GraphicsPreset) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Пресет",
                style = MaterialTheme.typography.titleSmall,
            )
            if (selectedPreset == GraphicsPreset.CUSTOM) {
                Text(
                    text = "Произвольный",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(
                GraphicsPreset.PERFORMANCE to "Производительность",
                GraphicsPreset.COMBAT to "Комбат",
                GraphicsPreset.BALANCE to "Баланс",
                GraphicsPreset.GRAPHICS to "Графика",
            ).forEach { (preset, label) ->
                val isSelected = selectedPreset == preset
                Button(
                    onClick = { onPresetSelected(preset) },
                    modifier = Modifier.weight(1f),
                    enabled = !isSelected,
                ) {
                    Text(label)
                }
            }
        }
    }
}

@Composable
private fun GraphicsSliders(
    state: GraphicsUiState,
    onShadowQualityChanged: (Int) -> Unit,
    onTextureQualityChanged: (Int) -> Unit,
    onLightingQualityChanged: (Int) -> Unit,
    onTreeQualityChanged: (Int) -> Unit,
    onWaterReflectionsChanged: (Int) -> Unit,
    onGrassQualityChanged: (Int) -> Unit,
    onCloudQualityChanged: (Int) -> Unit,
    onAntialiasingChanged: (Int) -> Unit,
) {
    val sliderItems = listOf(
        Triple("Качество теней", state.shadowQuality, onShadowQualityChanged),
        Triple("Качество текстур", state.textureQuality, onTextureQualityChanged),
        Triple("Качество освещения", state.lightingQuality, onLightingQualityChanged),
        Triple("Качество деревьев", state.treeQuality, onTreeQualityChanged),
        Triple("Отражения на воде", state.waterReflections, onWaterReflectionsChanged),
        Triple("Качество травы", state.grassQuality, onGrassQualityChanged),
        Triple("Качество облаков", state.cloudQuality, onCloudQualityChanged),
        Triple("Сглаживание", state.antialiasing, onAntialiasingChanged),
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Настройки графики",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        sliderItems.forEach { (label, value, onValueChange) ->
            QualitySliderRow(
                label = label,
                value = value,
                onValueChange = onValueChange,
            )
        }
    }
}

@Composable
private fun QualitySliderRow(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "$value%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt().coerceIn(0, 100)) },
            valueRange = 0f..100f,
            steps = 99,
        )
    }
}

@Composable
private fun GraphicsDiffSection(
    diffRows: List<DiffRow>,
) {
    val sharedScrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
    ) {
        GraphicsDiffColumn(
            title = "Исходный",
            diffRows = diffRows,
            isNewColumn = false,
            scrollState = sharedScrollState,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        GraphicsDiffColumn(
            title = "Измененный",
            diffRows = diffRows,
            isNewColumn = true,
            scrollState = sharedScrollState,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun GraphicsDiffColumn(
    title: String,
    diffRows: List<DiffRow>,
    isNewColumn: Boolean,
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Gray)
                .padding(12.dp)
                .verticalScroll(scrollState),
        ) {
            Column {
                diffRows.forEach { row ->
                    val text = if (isNewColumn) row.newLine.orEmpty() else row.oldLine.orEmpty()
                    val bg = when (row.type) {
                        DiffRowType.MODIFIED -> {
                            if (isNewColumn) Color(0x1A00AA00) else Color(0x1AAA0000)
                        }
                        DiffRowType.ADDED -> {
                            if (isNewColumn) Color(0x1A00AA00) else Color.Transparent
                        }
                        DiffRowType.REMOVED -> {
                            if (isNewColumn) Color(0x10AA0000) else Color(0x1AAA0000)
                        }
                        DiffRowType.UNCHANGED -> Color.Transparent
                    }
                    val displayLine = if (text.isEmpty()) " " else text

                    Text(
                        text = displayLine,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bg),
                        fontFamily = FontFamily.Monospace,
                    )
                }
                if (diffRows.isEmpty()) {
                    Text(
                        text = "Нет данных",
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        }
    }
}
