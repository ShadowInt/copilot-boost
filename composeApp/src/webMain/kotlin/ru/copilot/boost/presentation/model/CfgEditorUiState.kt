package ru.copilot.boost.presentation.model

import ru.copilot.boost.domain.model.DiffRow
import ru.copilot.boost.domain.model.DiffRowType
import ru.copilot.boost.model.UploadedFileData

data class CfgEditorUiState(
    val uploadedFile: UploadedFileData? = null,
    val isDragging: Boolean = false,
    val uploadError: String? = null,
    val disableParasiticParameters: Boolean = false,
    val disableLegsRendering: Boolean = false,
    val disableLegsDeformation: Boolean = false,
    val disableStrobeLights: Boolean = false,
    val reduceHeldItemSize: Boolean = false,
    val reduceCameraShake: Boolean = false,
    val improveTreeMarkerVisibility: Boolean = false,
    val disableOcclusionCullingSafeMode: Boolean = false,
    val disableGibsCompletely: Boolean = false,
    val patchedContent: String = "",
    val diffRows: List<DiffRow> = emptyList(),
) {
    val hasFile: Boolean get() = uploadedFile != null
    val fileName: String? get() = uploadedFile?.name
    val showDiff: Boolean get() = (
        disableParasiticParameters ||
            disableLegsRendering ||
            disableLegsDeformation ||
            disableStrobeLights ||
            reduceHeldItemSize ||
            reduceCameraShake ||
            improveTreeMarkerVisibility ||
            disableOcclusionCullingSafeMode ||
            disableGibsCompletely
        ) && diffRows.isNotEmpty()
    val hasChanges: Boolean get() = diffRows.any { it.type != DiffRowType.UNCHANGED }
    val downloadFileName: String? get() = uploadedFile?.name
}
