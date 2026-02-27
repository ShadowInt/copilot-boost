package ru.copilot.boost.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.copilot.boost.domain.CfgPatcher
import ru.copilot.boost.domain.model.AppliedPresetState
import ru.copilot.boost.domain.model.CfgPatchResult
import ru.copilot.boost.model.UploadedFileData
import ru.copilot.boost.presentation.model.CfgEditorUiState

class CfgEditorStore(
    private val cfgPatcher: CfgPatcher = CfgPatcher(),
) {
    var state by mutableStateOf(CfgEditorUiState())
        private set
    private var initiallyAppliedPresets: AppliedPresetState? = null

    fun onDragStateChanged(isDragging: Boolean) {
        state = state.copy(isDragging = isDragging)
    }

    fun onInvalidFile() {
        state = state.copy(uploadError = INVALID_FILE_ERROR)
    }

    fun onFileSelected(fileData: UploadedFileData) {
        val appliedPresets = cfgPatcher.detectAppliedPresets(fileData.content)
        initiallyAppliedPresets = appliedPresets
        state = state.copy(
            uploadedFile = fileData,
            uploadError = null,
            disableParasiticParameters = appliedPresets.disableParasiticParameters,
            disableLegsRendering = appliedPresets.disableLegsRendering,
            disableLegsDeformation = appliedPresets.disableLegsDeformation,
            disableStrobeLights = appliedPresets.disableStrobeLights,
            reduceHeldItemSize = appliedPresets.reduceHeldItemSize,
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

    fun onDisableStrobeLightsChanged(enabled: Boolean) {
        state = state.copy(disableStrobeLights = enabled)
        recalculatePatch()
    }

    fun onReduceHeldItemSizeChanged(enabled: Boolean) {
        state = state.copy(reduceHeldItemSize = enabled)
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
            initiallyAppliedPresets = null
            return
        }
        val initial = initiallyAppliedPresets

        val patchResult: CfgPatchResult = cfgPatcher.applyPresets(
            content = file.content,
            disableParasiticParameters = state.disableParasiticParameters,
            disableLegsRendering = state.disableLegsRendering,
            disableLegsDeformation = state.disableLegsDeformation,
            disableStrobeLights = state.disableStrobeLights,
            reduceHeldItemSize = state.reduceHeldItemSize,
            reduceCameraShake = state.reduceCameraShake,
            improveTreeMarkerVisibility = state.improveTreeMarkerVisibility,
            disableOcclusionCullingSafeMode = state.disableOcclusionCullingSafeMode,
            disableGibsCompletely = state.disableGibsCompletely,
            removeParasiticParameters = initial?.disableParasiticParameters == true && !state.disableParasiticParameters,
            removeLegsRendering = initial?.disableLegsRendering == true && !state.disableLegsRendering,
            removeLegsDeformation = initial?.disableLegsDeformation == true && !state.disableLegsDeformation,
            removeStrobeLights = initial?.disableStrobeLights == true && !state.disableStrobeLights,
            removeHeldItemSize = initial?.reduceHeldItemSize == true && !state.reduceHeldItemSize,
            removeCameraShake = initial?.reduceCameraShake == true && !state.reduceCameraShake,
            removeTreeMarkerVisibility = initial?.improveTreeMarkerVisibility == true && !state.improveTreeMarkerVisibility,
            removeOcclusionCullingSafeMode = initial?.disableOcclusionCullingSafeMode == true && !state.disableOcclusionCullingSafeMode,
            removeGibsCompletely = initial?.disableGibsCompletely == true && !state.disableGibsCompletely,
        )

        state = state.copy(
            patchedContent = patchResult.updatedContent,
            diffRows = patchResult.diffRows,
        )
    }

    companion object {
        const val INVALID_FILE_ERROR: String = "Разрешен только файл с именем client.cfg"
    }
}
