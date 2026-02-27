package ru.copilot.boost.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.copilot.boost.domain.CfgPatcher
import ru.copilot.boost.domain.model.AppliedPresetState
import ru.copilot.boost.domain.model.CfgPatchResult
import ru.copilot.boost.domain.model.PreparedCfgContent
import ru.copilot.boost.model.UploadedFileData
import ru.copilot.boost.presentation.model.CfgEditorUiState

class CfgEditorStore(
    private val cfgPatcher: CfgPatcher = CfgPatcher(),
) {
    var state by mutableStateOf(CfgEditorUiState())
        private set
    private var initiallyAppliedPresets: AppliedPresetState? = null
    private var preparedContent: PreparedCfgContent? = null

    fun onDragStateChanged(isDragging: Boolean) {
        if (state.isDragging == isDragging) return
        state = state.copy(isDragging = isDragging)
    }

    fun onInvalidFile() {
        if (state.uploadError == INVALID_FILE_ERROR) return
        state = state.copy(uploadError = INVALID_FILE_ERROR)
    }

    fun onFileSelected(fileData: UploadedFileData) {
        val appliedPresets = cfgPatcher.detectAppliedPresets(fileData.content)
        initiallyAppliedPresets = appliedPresets
        preparedContent = cfgPatcher.prepareContent(fileData.content)
        state = state.copy(
            uploadedFile = fileData,
            uploadError = null,
            disableParasiticParameters = appliedPresets.disableParasiticParameters,
            disableLegsRendering = appliedPresets.disableLegsRendering,
            disableLegsDeformation = appliedPresets.disableLegsDeformation,
            disableStrobeLights = appliedPresets.disableStrobeLights,
            reduceHeldItemSize = appliedPresets.reduceHeldItemSize,
            restoreEventTextNotifications = appliedPresets.restoreEventTextNotifications,
            removeAutocraftMenuDelay = appliedPresets.removeAutocraftMenuDelay,
            reduceSleepingBagRemovalDelay = appliedPresets.reduceSleepingBagRemovalDelay,
            addMapInfoToF8Menu = appliedPresets.addMapInfoToF8Menu,
            disableClientErrorOverlay = appliedPresets.disableClientErrorOverlay,
            addAdminGesturesToGameMenu = appliedPresets.addAdminGesturesToGameMenu,
            convenientSkinSorting = appliedPresets.convenientSkinSorting,
            enlargedConsole = appliedPresets.enlargedConsole,
            reduceCameraShake = appliedPresets.reduceCameraShake,
            improveTreeMarkerVisibility = appliedPresets.improveTreeMarkerVisibility,
            disableOcclusionCullingSafeMode = appliedPresets.disableOcclusionCullingSafeMode,
            disableGibsCompletely = appliedPresets.disableGibsCompletely,
        )
        recalculatePatch()
    }

    fun onDisableParasiticChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.disableParasiticParameters) {
            it.copy(disableParasiticParameters = enabled)
        }
    }

    fun onDisableLegsRenderingChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.disableLegsRendering) {
            it.copy(disableLegsRendering = enabled)
        }
    }

    fun onDisableLegsDeformationChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.disableLegsDeformation) {
            it.copy(disableLegsDeformation = enabled)
        }
    }

    fun onDisableStrobeLightsChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.disableStrobeLights) {
            it.copy(disableStrobeLights = enabled)
        }
    }

    fun onReduceHeldItemSizeChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.reduceHeldItemSize) {
            it.copy(reduceHeldItemSize = enabled)
        }
    }

    fun onRestoreEventTextNotificationsChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.restoreEventTextNotifications) {
            it.copy(restoreEventTextNotifications = enabled)
        }
    }

    fun onRemoveAutocraftMenuDelayChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.removeAutocraftMenuDelay) {
            it.copy(removeAutocraftMenuDelay = enabled)
        }
    }

    fun onReduceSleepingBagRemovalDelayChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.reduceSleepingBagRemovalDelay) {
            it.copy(reduceSleepingBagRemovalDelay = enabled)
        }
    }

    fun onAddMapInfoToF8MenuChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.addMapInfoToF8Menu) {
            it.copy(addMapInfoToF8Menu = enabled)
        }
    }

    fun onDisableClientErrorOverlayChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.disableClientErrorOverlay) {
            it.copy(disableClientErrorOverlay = enabled)
        }
    }

    fun onAddAdminGesturesToGameMenuChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.addAdminGesturesToGameMenu) {
            it.copy(addAdminGesturesToGameMenu = enabled)
        }
    }

    fun onConvenientSkinSortingChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.convenientSkinSorting) {
            it.copy(convenientSkinSorting = enabled)
        }
    }

    fun onEnlargedConsoleChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.enlargedConsole) {
            it.copy(enlargedConsole = enabled)
        }
    }

    fun onReduceCameraShakeChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.reduceCameraShake) {
            it.copy(reduceCameraShake = enabled)
        }
    }

    fun onImproveTreeMarkerVisibilityChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.improveTreeMarkerVisibility) {
            it.copy(improveTreeMarkerVisibility = enabled)
        }
    }

    fun onDisableOcclusionCullingSafeModeChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.disableOcclusionCullingSafeMode) {
            it.copy(disableOcclusionCullingSafeMode = enabled)
        }
    }

    fun onDisableGibsCompletelyChanged(enabled: Boolean) {
        updateAndRecalculateIfChanged(enabled, state.disableGibsCompletely) {
            it.copy(disableGibsCompletely = enabled)
        }
    }

    private fun recalculatePatch() {
        val file = state.uploadedFile ?: run {
            state = state.copy(
                patchedContent = "",
                diffRows = emptyList(),
            )
            initiallyAppliedPresets = null
            preparedContent = null
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
            restoreEventTextNotifications = state.restoreEventTextNotifications,
            removeAutocraftMenuDelay = state.removeAutocraftMenuDelay,
            reduceSleepingBagRemovalDelay = state.reduceSleepingBagRemovalDelay,
            addMapInfoToF8Menu = state.addMapInfoToF8Menu,
            disableClientErrorOverlay = state.disableClientErrorOverlay,
            addAdminGesturesToGameMenu = state.addAdminGesturesToGameMenu,
            convenientSkinSorting = state.convenientSkinSorting,
            enlargedConsole = state.enlargedConsole,
            reduceCameraShake = state.reduceCameraShake,
            improveTreeMarkerVisibility = state.improveTreeMarkerVisibility,
            disableOcclusionCullingSafeMode = state.disableOcclusionCullingSafeMode,
            disableGibsCompletely = state.disableGibsCompletely,
            removeParasiticParameters = initial?.disableParasiticParameters == true && !state.disableParasiticParameters,
            removeLegsRendering = initial?.disableLegsRendering == true && !state.disableLegsRendering,
            removeLegsDeformation = initial?.disableLegsDeformation == true && !state.disableLegsDeformation,
            removeStrobeLights = initial?.disableStrobeLights == true && !state.disableStrobeLights,
            removeHeldItemSize = initial?.reduceHeldItemSize == true && !state.reduceHeldItemSize,
            removeEventTextNotifications = initial?.restoreEventTextNotifications == true && !state.restoreEventTextNotifications,
            removeQuickCraftDelay = initial?.removeAutocraftMenuDelay == true && !state.removeAutocraftMenuDelay,
            removeSleepingBagRemovalDelay = initial?.reduceSleepingBagRemovalDelay == true && !state.reduceSleepingBagRemovalDelay,
            removeMapInfoFromF8Menu = initial?.addMapInfoToF8Menu == true && !state.addMapInfoToF8Menu,
            removeClientErrorOverlay = initial?.disableClientErrorOverlay == true && !state.disableClientErrorOverlay,
            removeAdminGesturesFromGameMenu = initial?.addAdminGesturesToGameMenu == true && !state.addAdminGesturesToGameMenu,
            removeConvenientSkinSorting = initial?.convenientSkinSorting == true && !state.convenientSkinSorting,
            removeEnlargedConsole = initial?.enlargedConsole == true && !state.enlargedConsole,
            removeCameraShake = initial?.reduceCameraShake == true && !state.reduceCameraShake,
            removeTreeMarkerVisibility = initial?.improveTreeMarkerVisibility == true && !state.improveTreeMarkerVisibility,
            removeOcclusionCullingSafeMode = initial?.disableOcclusionCullingSafeMode == true && !state.disableOcclusionCullingSafeMode,
            removeGibsCompletely = initial?.disableGibsCompletely == true && !state.disableGibsCompletely,
            preparedContent = preparedContent,
        )

        state = state.copy(
            patchedContent = patchResult.updatedContent,
            diffRows = patchResult.diffRows,
        )
    }

    private inline fun updateAndRecalculateIfChanged(
        newValue: Boolean,
        currentValue: Boolean,
        update: (CfgEditorUiState) -> CfgEditorUiState,
    ) {
        if (newValue == currentValue) return
        state = update(state)
        recalculatePatch()
    }

    companion object {
        const val INVALID_FILE_ERROR: String = "Разрешен только файл с именем client.cfg"
    }
}
