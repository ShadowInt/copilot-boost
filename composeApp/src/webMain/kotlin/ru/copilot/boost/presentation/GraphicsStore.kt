package ru.copilot.boost.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.copilot.boost.domain.model.GraphicsPresetId
import ru.copilot.boost.domain.model.GraphicsSettingId
import ru.copilot.boost.domain.model.GraphicsSettings
import ru.copilot.boost.model.UploadedFileData
import ru.copilot.boost.presentation.model.GraphicsUiState

class GraphicsStore : SetupModuleStore {
    var state by mutableStateOf(GraphicsUiState())
        private set

    fun initWithFile(fileData: UploadedFileData) {
        val detected = GraphicsSettings.detectFromCfg(fileData.content)
        state = GraphicsUiState(
            settings = detected,
            initialSettings = detected,
            isInitialized = true,
        )
    }

    fun onSettingChanged(id: GraphicsSettingId, value: Int) {
        if (state.settings[id] == value) return
        state = state.copy(settings = state.settings.with(id, value))
    }

    fun onPresetSelected(presetId: GraphicsPresetId) {
        state = state.copy(settings = GraphicsSettings.fromPreset(presetId))
    }

    override fun reset() {
        state = GraphicsUiState()
    }
}
