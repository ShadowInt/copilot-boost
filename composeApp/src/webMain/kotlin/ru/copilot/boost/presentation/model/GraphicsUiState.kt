package ru.copilot.boost.presentation.model

import ru.copilot.boost.domain.model.DiffRow
import ru.copilot.boost.domain.model.DiffRowType
import ru.copilot.boost.model.UploadedFileData

enum class GraphicsPreset {
    PERFORMANCE,
    COMBAT,
    BALANCE,
    GRAPHICS,
    CUSTOM,
}

data class GraphicsUiState(
    val uploadedFile: UploadedFileData? = null,
    val isDragging: Boolean = false,
    val uploadError: String? = null,
    val selectedPreset: GraphicsPreset = GraphicsPreset.BALANCE,
    val shadowQuality: Int = 50,
    val textureQuality: Int = 50,
    val lightingQuality: Int = 50,
    val treeQuality: Int = 50,
    val waterReflections: Int = 50,
    val grassQuality: Int = 50,
    val cloudQuality: Int = 50,
    val antialiasing: Int = 50,
    val patchedContent: String = "",
    val diffRows: List<DiffRow> = emptyList(),
) {
    val hasFile: Boolean get() = uploadedFile != null
    val fileName: String? get() = uploadedFile?.name
    val showDiff: Boolean get() = hasFile && diffRows.isNotEmpty()
    val hasChanges: Boolean get() = diffRows.any { it.type != DiffRowType.UNCHANGED }
}
