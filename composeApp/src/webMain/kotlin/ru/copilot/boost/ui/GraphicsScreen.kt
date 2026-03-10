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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.domain.model.GraphicsPresetId
import ru.copilot.boost.domain.model.GraphicsSettingId
import ru.copilot.boost.presentation.model.GraphicsUiState
import kotlin.math.roundToInt

@Composable
fun GraphicsScreen(
    state: GraphicsUiState,
    onSettingChanged: (GraphicsSettingId, Int) -> Unit,
    onPresetSelected: (GraphicsPresetId) -> Unit,
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
        if (!state.isInitialized) {
            Text(
                text = stringResource(Res.string.cfg_no_file),
                modifier = Modifier.padding(top = 12.dp),
            )
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            PresetsSection(
                activePreset = state.activePreset,
                onPresetSelected = onPresetSelected,
            )

            Spacer(modifier = Modifier.height(20.dp))

            SlidersSection(
                state = state,
                onSettingChanged = onSettingChanged,
            )
        }
    }
}

@Composable
private fun PresetsSection(
    activePreset: GraphicsPresetId?,
    onPresetSelected: (GraphicsPresetId) -> Unit,
) {
    Text(
        text = stringResource(Res.string.graphics_presets_title),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 12.dp),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PresetButton(
            text = stringResource(Res.string.graphics_preset_performance),
            isActive = activePreset == GraphicsPresetId.Performance,
            onClick = { onPresetSelected(GraphicsPresetId.Performance) },
            modifier = Modifier.weight(1f),
        )
        PresetButton(
            text = stringResource(Res.string.graphics_preset_combat),
            isActive = activePreset == GraphicsPresetId.Combat,
            onClick = { onPresetSelected(GraphicsPresetId.Combat) },
            modifier = Modifier.weight(1f),
        )
        PresetButton(
            text = stringResource(Res.string.graphics_preset_balance),
            isActive = activePreset == GraphicsPresetId.Balance,
            onClick = { onPresetSelected(GraphicsPresetId.Balance) },
            modifier = Modifier.weight(1f),
        )
        PresetButton(
            text = stringResource(Res.string.graphics_preset_graphics),
            isActive = activePreset == GraphicsPresetId.Graphics,
            onClick = { onPresetSelected(GraphicsPresetId.Graphics) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PresetButton(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isActive) {
        Button(
            onClick = onClick,
            modifier = modifier,
        ) {
            Text(text)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
        ) {
            Text(text)
        }
    }
}

@Composable
private fun SlidersSection(
    state: GraphicsUiState,
    onSettingChanged: (GraphicsSettingId, Int) -> Unit,
) {
    Text(
        text = stringResource(Res.string.graphics_settings_title),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray)
            .padding(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            GraphicsSliderRow(
                label = stringResource(Res.string.graphics_shadow_quality),
                value = state.settings[GraphicsSettingId.ShadowQuality],
                maxValue = GraphicsSettingId.ShadowQuality.maxLevel,
                onValueChange = { onSettingChanged(GraphicsSettingId.ShadowQuality, it) },
            )
            GraphicsSliderRow(
                label = stringResource(Res.string.graphics_texture_quality),
                value = state.settings[GraphicsSettingId.TextureQuality],
                maxValue = GraphicsSettingId.TextureQuality.maxLevel,
                onValueChange = { onSettingChanged(GraphicsSettingId.TextureQuality, it) },
            )
            GraphicsSliderRow(
                label = stringResource(Res.string.graphics_lighting_quality),
                value = state.settings[GraphicsSettingId.LightingQuality],
                maxValue = GraphicsSettingId.LightingQuality.maxLevel,
                onValueChange = { onSettingChanged(GraphicsSettingId.LightingQuality, it) },
            )
            GraphicsSliderRow(
                label = stringResource(Res.string.graphics_tree_quality),
                value = state.settings[GraphicsSettingId.TreeQuality],
                maxValue = GraphicsSettingId.TreeQuality.maxLevel,
                onValueChange = { onSettingChanged(GraphicsSettingId.TreeQuality, it) },
            )
            GraphicsSliderRow(
                label = stringResource(Res.string.graphics_water_reflections),
                value = state.settings[GraphicsSettingId.WaterReflections],
                maxValue = GraphicsSettingId.WaterReflections.maxLevel,
                onValueChange = { onSettingChanged(GraphicsSettingId.WaterReflections, it) },
            )
            GraphicsSliderRow(
                label = stringResource(Res.string.graphics_grass_quality),
                value = state.settings[GraphicsSettingId.GrassQuality],
                maxValue = GraphicsSettingId.GrassQuality.maxLevel,
                onValueChange = { onSettingChanged(GraphicsSettingId.GrassQuality, it) },
            )
            GraphicsSliderRow(
                label = stringResource(Res.string.graphics_cloud_quality),
                value = state.settings[GraphicsSettingId.CloudQuality],
                maxValue = GraphicsSettingId.CloudQuality.maxLevel,
                onValueChange = { onSettingChanged(GraphicsSettingId.CloudQuality, it) },
            )
            GraphicsSliderRow(
                label = stringResource(Res.string.graphics_antialiasing),
                value = state.settings[GraphicsSettingId.AntiAliasing],
                maxValue = GraphicsSettingId.AntiAliasing.maxLevel,
                onValueChange = { onSettingChanged(GraphicsSettingId.AntiAliasing, it) },
            )
        }
    }
}

@Composable
private fun GraphicsSliderRow(
    label: String,
    value: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(200.dp),
        )
        Text(
            text = stringResource(Res.string.graphics_level_min),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp),
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = 0f..maxValue.toFloat(),
            steps = if (maxValue > 1) maxValue - 1 else 0,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(Res.string.graphics_level_max),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp),
        )
        Text(
            text = "$value/$maxValue",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .width(40.dp)
                .padding(start = 8.dp),
        )
    }
}
