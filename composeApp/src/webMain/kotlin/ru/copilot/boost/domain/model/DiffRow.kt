package ru.copilot.boost.domain.model

data class DiffRow(
    val type: DiffRowType,
    val oldLine: String?,
    val newLine: String?,
)

enum class DiffRowType {
    UNCHANGED,
    MODIFIED,
    ADDED,
    REMOVED,
}
