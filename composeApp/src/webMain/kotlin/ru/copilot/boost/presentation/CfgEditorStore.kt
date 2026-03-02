package ru.copilot.boost.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.copilot.boost.domain.CfgPatcher
import ru.copilot.boost.domain.model.PresetFlags
import ru.copilot.boost.domain.model.PresetId
import ru.copilot.boost.domain.model.PreparedCfgContent
import ru.copilot.boost.model.UploadedFileData
import ru.copilot.boost.presentation.model.CfgEditorUiState
import ru.copilot.boost.presentation.model.CfgUploadError

class CfgEditorStore(
    private val cfgPatcher: CfgPatcher = CfgPatcher(),
) : SetupModuleStore {
    var state by mutableStateOf(CfgEditorUiState())
        private set
    private var initiallyAppliedPresets: PresetFlags? = null
    private var preparedContent: PreparedCfgContent? = null

    fun onDragStateChanged(isDragging: Boolean) {
        if (state.isDragging == isDragging) return
        state = state.copy(isDragging = isDragging)
    }

    fun onInvalidFile() {
        if (state.uploadError == CfgUploadError.InvalidFileName) return
        state = state.copy(uploadError = CfgUploadError.InvalidFileName)
    }

    fun onReadError() {
        if (state.uploadError == CfgUploadError.ReadFailed) return
        state = state.copy(uploadError = CfgUploadError.ReadFailed)
    }

    fun onFileSelected(fileData: UploadedFileData) {
        val appliedPresets = cfgPatcher.detectAppliedPresets(fileData.content)
        initiallyAppliedPresets = appliedPresets
        preparedContent = cfgPatcher.prepareContent(fileData.content)
        state = state.copy(
            uploadedFile = fileData,
            uploadError = null,
            presets = appliedPresets,
        )
        recalculatePatch()
    }

    fun onPresetChanged(id: PresetId, enabled: Boolean) {
        if (state.presets[id] == enabled) return
        state = state.copy(presets = state.presets.with(id, enabled))
        recalculatePatch()
    }

    override fun reset() {
        state = CfgEditorUiState()
        initiallyAppliedPresets = null
        preparedContent = null
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

        val removeFlags = if (initial != null) {
            PresetFlags(
                PresetId.entries.associateWith { id ->
                    initial[id] && !state.presets[id]
                },
            )
        } else {
            PresetFlags()
        }

        val patchResult = cfgPatcher.applyPresets(
            content = file.content,
            enabled = state.presets,
            remove = removeFlags,
            preparedContent = preparedContent,
        )

        state = state.copy(
            patchedContent = patchResult.updatedContent,
            diffRows = patchResult.diffRows,
        )
    }
}
