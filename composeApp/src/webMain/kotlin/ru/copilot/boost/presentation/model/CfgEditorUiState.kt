package ru.copilot.boost.presentation.model

import ru.copilot.boost.domain.model.DiffRow
import ru.copilot.boost.domain.model.DiffRowType
import ru.copilot.boost.domain.model.PresetFlags
import ru.copilot.boost.model.UploadedFileData

data class CfgEditorUiState(
    val uploadedFile: UploadedFileData? = null,
    val isDragging: Boolean = false,
    val uploadError: String? = null,
    val presets: PresetFlags = PresetFlags(),
    val patchedContent: String = "",
    val diffRows: List<DiffRow> = emptyList(),
) {
    val hasFile: Boolean get() = uploadedFile != null
    val fileName: String? get() = uploadedFile?.name
    val hasChanges: Boolean get() = diffRows.any { it.type != DiffRowType.UNCHANGED }
    val downloadFileName: String? get() = uploadedFile?.name
}
