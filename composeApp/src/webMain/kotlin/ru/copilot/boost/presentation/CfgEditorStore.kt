package ru.copilot.boost.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.copilot.boost.domain.CfgPatcher
import ru.copilot.boost.domain.model.CfgPatchResult
import ru.copilot.boost.model.UploadedFileData
import ru.copilot.boost.presentation.model.CfgEditorUiState

class CfgEditorStore(
    private val cfgPatcher: CfgPatcher = CfgPatcher(),
) {
    var state by mutableStateOf(CfgEditorUiState())
        private set

    fun onDragStateChanged(isDragging: Boolean) {
        state = state.copy(isDragging = isDragging)
    }

    fun onInvalidFile() {
        state = state.copy(uploadError = INVALID_FILE_ERROR)
    }

    fun onFileSelected(fileData: UploadedFileData) {
        val appliedPresets = cfgPatcher.detectAppliedPresets(fileData.content)
        state = state.copy(
            uploadedFile = fileData,
            uploadError = null,
            disableParasiticParameters = appliedPresets.disableParasiticParameters,
            disableLegsRendering = appliedPresets.disableLegsRendering,
            disableLegsDeformation = appliedPresets.disableLegsDeformation,
            reduceCameraShake = appliedPresets.reduceCameraShake,
            improveTreeMarkerVisibility = appliedPresets.improveTreeMarkerVisibility,
            disableOcclusionCullingSafeMode = appliedPresets.disableOcclusionCullingSafeMode,
            disableGibsCompletely = appliedPresets.disableGibsCompletely,
        )
        recalculatePatch()
    }

    fun onDisableParasiticChanged(enabled: Boolean) {
        state = state.copy(disableParasiticParameters = enabled)
        recalculatePatch()
    }

    fun onDisableLegsRenderingChanged(enabled: Boolean) {
        state = state.copy(disableLegsRendering = enabled)
        recalculatePatch()
    }

    fun onDisableLegsDeformationChanged(enabled: Boolean) {
        state = state.copy(disableLegsDeformation = enabled)
        recalculatePatch()
    }

    fun onReduceCameraShakeChanged(enabled: Boolean) {
        state = state.copy(reduceCameraShake = enabled)
        recalculatePatch()
    }

    fun onImproveTreeMarkerVisibilityChanged(enabled: Boolean) {
        state = state.copy(improveTreeMarkerVisibility = enabled)
        recalculatePatch()
    }

    fun onDisableOcclusionCullingSafeModeChanged(enabled: Boolean) {
        state = state.copy(disableOcclusionCullingSafeMode = enabled)
        recalculatePatch()
    }

    fun onDisableGibsCompletelyChanged(enabled: Boolean) {
        state = state.copy(disableGibsCompletely = enabled)
        recalculatePatch()
    }

    private fun recalculatePatch() {
        val file = state.uploadedFile ?: run {
            state = state.copy(
                patchedContent = "",
                diffRows = emptyList(),
            )
            return
        }

        val patchResult: CfgPatchResult = cfgPatcher.applyPresets(
            content = file.content,
            disableParasiticParameters = state.disableParasiticParameters,
            disableLegsRendering = state.disableLegsRendering,
            disableLegsDeformation = state.disableLegsDeformation,
            reduceCameraShake = state.reduceCameraShake,
            improveTreeMarkerVisibility = state.improveTreeMarkerVisibility,
            disableOcclusionCullingSafeMode = state.disableOcclusionCullingSafeMode,
            disableGibsCompletely = state.disableGibsCompletely,
        )

        state = state.copy(
            patchedContent = patchResult.updatedContent,
            diffRows = patchResult.diffRows,
        )
    }

    companion object {
        const val INVALID_FILE_ERROR: String = "Разрешены только файлы с расширением .cfg"
    }
}
