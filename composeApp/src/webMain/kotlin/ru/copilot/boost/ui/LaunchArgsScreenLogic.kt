package ru.copilot.boost.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class EditorColumns(
    val left: Dp,
    val right: Dp,
)

internal object LaunchArgsUiSpec {
    val ScreenPadding = 24.dp
    val MainGap = 16.dp
    val InnerGap = 8.dp
    val CardHeaderHeight = 44.dp
    val CardHeaderBottomPadding = 6.dp
    const val STAGE_SWITCH_THRESHOLD_PX = 24
    val CopyFieldMinHeight = 48.dp
    val CopyFieldMaxHeight = 180.dp
}

internal fun calculateColumns(maxWidth: Dp): EditorColumns {
    val baseLeftWidth = maxWidth * 0.33f
    val baseRemainingWidth = maxWidth - baseLeftWidth - LaunchArgsUiSpec.MainGap
    val rightWidth = ((baseRemainingWidth - LaunchArgsUiSpec.InnerGap) / 2) * 2 - 40.dp
    val leftWidth = maxWidth - rightWidth - LaunchArgsUiSpec.MainGap
    return EditorColumns(left = leftWidth, right = rightWidth)
}

internal fun buildLaunchArgs(
    adminTeleport: Boolean,
    fasterAltHeadTurn: Boolean,
    disablePlayerEyesAnimation: Boolean,
    serverHitmarker: Boolean,
    oldItemPickupNotifications: Boolean,
): String = buildList {
    if (adminTeleport) add("-global.enable_marker_teleport \"True\"")
    if (fasterAltHeadTurn) {
        add("-client.headlerp \"10\"")
        add("-headlerp_inertia \"0\"")
    }
    if (disablePlayerEyesAnimation) {
        add("-player.eye_blinking \"False\"")
        add("-player.eye_movement \"False\"")
    }
    if (serverHitmarker) add("-hitnotify.notification_level \"2\"")
    if (oldItemPickupNotifications) {
        add("-global.showitempickupnotices \"1\"")
        add("-global.showitemcountsonpickup \"False\"")
        add("-global.usesingleitempickupnotice \"False\"")
    }
}.joinToString(" ")

internal data class StageProgress(
    val activeStageIndex: Int,
    val isAtBottom: Boolean,
    val lastStageIndex: Int,
)

internal fun computeStageProgress(
    stageHeightsPx: List<Int>,
    scrollValue: Int,
    viewportHeightPx: Int,
    stageSwitchThresholdPx: Int = LaunchArgsUiSpec.STAGE_SWITCH_THRESHOLD_PX,
    maxScrollValue: Int,
): StageProgress {
    val totalStages = stageHeightsPx.size
    val stageEnds = buildList {
        var sum = 0
        stageHeightsPx.forEach { height ->
            sum += height
            add(sum)
        }
    }
    val viewportBottom = scrollValue + viewportHeightPx
    val passedStagesCount = stageEnds.count { end -> viewportBottom >= end - stageSwitchThresholdPx }
    val activeStageIndex = passedStagesCount.coerceAtMost(totalStages - 1)
    val isAtBottom = scrollValue >= (maxScrollValue - stageSwitchThresholdPx).coerceAtLeast(0)
    return StageProgress(
        activeStageIndex = activeStageIndex,
        isAtBottom = isAtBottom,
        lastStageIndex = totalStages - 1,
    )
}

internal fun statusForStage(
    index: Int,
    hasSelectedSettings: Boolean,
    isCurrentSelectionCopied: Boolean,
    progress: StageProgress,
): LaunchStageStatus {
    val effectiveActiveStageIndex = if (isCurrentSelectionCopied) {
        progress.activeStageIndex.coerceAtLeast(1)
    } else {
        progress.activeStageIndex
    }
    return when {
        !hasSelectedSettings -> LaunchStageStatus.NOT_STARTED
        !isCurrentSelectionCopied && index == 0 -> LaunchStageStatus.IN_PROGRESS
        !isCurrentSelectionCopied -> LaunchStageStatus.NOT_STARTED
        progress.isAtBottom && index == progress.lastStageIndex -> LaunchStageStatus.COMPLETED
        index < effectiveActiveStageIndex -> LaunchStageStatus.COMPLETED
        index == effectiveActiveStageIndex -> LaunchStageStatus.IN_PROGRESS
        else -> LaunchStageStatus.NOT_STARTED
    }
}
