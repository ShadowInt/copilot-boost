package ru.copilot.boost.domain.model

data class CfgPatchResult(
    val updatedContent: String,
    val diffRows: List<DiffRow>,
)
