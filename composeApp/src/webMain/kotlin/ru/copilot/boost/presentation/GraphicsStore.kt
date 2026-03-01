package ru.copilot.boost.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.copilot.boost.domain.GraphicsConfigParser
import ru.copilot.boost.model.UploadedFileData
import ru.copilot.boost.presentation.model.GraphicsPreset
import ru.copilot.boost.presentation.model.GraphicsUiState

class GraphicsStore {
    var state by mutableStateOf(GraphicsUiState())
        private set

    fun onDragStateChanged(isDragging: Boolean) {
        if (state.isDragging == isDragging) return
        state = state.copy(isDragging = isDragging)
    }

    fun onInvalidFile() {
        if (state.uploadError == INVALID_FILE_ERROR) return
        state = state.copy(uploadError = INVALID_FILE_ERROR)
    }

    fun onFileSelected(fileData: UploadedFileData) {
        val parsed = GraphicsConfigParser.parseFromContent(fileData.content)
        state = state.copy(
            uploadedFile = fileData,
            uploadError = null,
            shadowQuality = parsed.shadowQuality,
            textureQuality = parsed.textureQuality,
            lightingQuality = parsed.lightingQuality,
            treeQuality = parsed.treeQuality,
            waterReflections = parsed.waterReflections,
            grassQuality = parsed.grassQuality,
            cloudQuality = parsed.cloudQuality,
            antialiasing = parsed.antialiasing,
            selectedPreset = detectPreset(parsed),
        )
        recalculateContent()
    }

    private fun detectPreset(parsed: GraphicsUiState): GraphicsPreset {
        return GraphicsPreset.entries
            .filter { it != GraphicsPreset.CUSTOM }
            .firstOrNull { preset ->
                val presetState = GraphicsConfigParser.presetToState(preset)
                presetState.shadowQuality == parsed.shadowQuality &&
                    presetState.textureQuality == parsed.textureQuality &&
                    presetState.lightingQuality == parsed.lightingQuality &&
                    presetState.treeQuality == parsed.treeQuality &&
                    presetState.waterReflections == parsed.waterReflections &&
                    presetState.grassQuality == parsed.grassQuality &&
                    presetState.cloudQuality == parsed.cloudQuality &&
                    presetState.antialiasing == parsed.antialiasing
            } ?: GraphicsPreset.CUSTOM
    }

    fun onPresetSelected(preset: GraphicsPreset) {
        if (preset == GraphicsPreset.CUSTOM) return
        val presetState = GraphicsConfigParser.presetToState(preset)
        state = state.copy(
            selectedPreset = preset,
            shadowQuality = presetState.shadowQuality,
            textureQuality = presetState.textureQuality,
            lightingQuality = presetState.lightingQuality,
            treeQuality = presetState.treeQuality,
            waterReflections = presetState.waterReflections,
            grassQuality = presetState.grassQuality,
            cloudQuality = presetState.cloudQuality,
            antialiasing = presetState.antialiasing,
        )
        recalculateContent()
    }

    fun onShadowQualityChanged(value: Int) = updateSlider(value) { it.copy(shadowQuality = value, selectedPreset = GraphicsPreset.CUSTOM) }
    fun onTextureQualityChanged(value: Int) = updateSlider(value) { it.copy(textureQuality = value, selectedPreset = GraphicsPreset.CUSTOM) }
    fun onLightingQualityChanged(value: Int) = updateSlider(value) { it.copy(lightingQuality = value, selectedPreset = GraphicsPreset.CUSTOM) }
    fun onTreeQualityChanged(value: Int) = updateSlider(value) { it.copy(treeQuality = value, selectedPreset = GraphicsPreset.CUSTOM) }
    fun onWaterReflectionsChanged(value: Int) = updateSlider(value) { it.copy(waterReflections = value, selectedPreset = GraphicsPreset.CUSTOM) }
    fun onGrassQualityChanged(value: Int) = updateSlider(value) { it.copy(grassQuality = value, selectedPreset = GraphicsPreset.CUSTOM) }
    fun onCloudQualityChanged(value: Int) = updateSlider(value) { it.copy(cloudQuality = value, selectedPreset = GraphicsPreset.CUSTOM) }
    fun onAntialiasingChanged(value: Int) = updateSlider(value) { it.copy(antialiasing = value, selectedPreset = GraphicsPreset.CUSTOM) }

    private inline fun updateSlider(value: Int, update: (GraphicsUiState) -> GraphicsUiState) {
        state = update(state)
        recalculateContent()
    }

    private fun recalculateContent() {
        val file = state.uploadedFile ?: run {
            state = state.copy(
                patchedContent = "",
                diffRows = emptyList(),
            )
            return
        }
        val result = GraphicsConfigParser.applyToContent(file.content, state)
        state = state.copy(
            patchedContent = result.updatedContent,
            diffRows = result.diffRows,
        )
    }

    val patchedContent: String
        get() = if (state.patchedContent.isNotEmpty()) {
            state.patchedContent
        } else {
            state.uploadedFile?.content.orEmpty()
        }

    companion object {
        const val INVALID_FILE_ERROR: String = "Разрешен только файл с именем client.cfg"
    }
}
