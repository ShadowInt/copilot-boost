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
