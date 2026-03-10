package ru.copilot.boost.presentation.model

import ru.copilot.boost.domain.model.GraphicsPresetId
import ru.copilot.boost.domain.model.GraphicsSettings

data class GraphicsUiState(
    val settings: GraphicsSettings = GraphicsSettings(),
    val initialSettings: GraphicsSettings = GraphicsSettings(),
    val isInitialized: Boolean = false,
) {
    val activePreset: GraphicsPresetId? get() = settings.matchingPreset()
    val hasChanges: Boolean get() = isInitialized && settings != initialSettings
}
