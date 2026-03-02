package ru.copilot.boost.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class EditorColumns(
    val left: Dp,
    val right: Dp,
)

internal object LaunchArgsUiSpec {
    val ScreenPadding = 24.dp
    val ColumnGap = 8.dp
    val CardHeaderHeight = 44.dp
    val CardHeaderBottomPadding = 6.dp
}

internal fun calculateColumns(maxWidth: Dp): EditorColumns {
    val gap = LaunchArgsUiSpec.ColumnGap
    val leftWidth = maxWidth * 0.33f
    val rightWidth = maxWidth - leftWidth - gap
    return EditorColumns(left = leftWidth, right = rightWidth)
}
