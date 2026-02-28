package ru.copilot.boost.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.copilot.boost.presentation.model.LaunchArgsCopyState
import ru.copilot.boost.presentation.model.LaunchArgsUiState
import ru.copilot.boost.ui.buildLaunchArgs

class LaunchArgsStore(
    initialState: LaunchArgsUiState = LaunchArgsUiState(),
) {
    var state by mutableStateOf(initialState)
        private set

    val launchArgs: String
        get() = buildLaunchArgs(
            adminTeleport = state.adminTeleport,
            fasterAltHeadTurn = state.fasterAltHeadTurn,
            disablePlayerEyesAnimation = state.disablePlayerEyesAnimation,
            serverHitmarker = state.serverHitmarker,
            oldItemPickupNotifications = state.oldItemPickupNotifications,
        )

    val hasSelectedSettings: Boolean
        get() = launchArgs.isNotBlank()

    val isCurrentSelectionCopied: Boolean
        get() = hasSelectedSettings && state.copyState.hasCopied

    fun onAdminTeleportChanged(enabled: Boolean) {
        if (state.adminTeleport == enabled) return
        state = state.copy(
            adminTeleport = enabled,
            copyState = LaunchArgsCopyState(),
        )
    }

    fun onFasterAltHeadTurnChanged(enabled: Boolean) {
        if (state.fasterAltHeadTurn == enabled) return
        state = state.copy(
            fasterAltHeadTurn = enabled,
            copyState = LaunchArgsCopyState(),
        )
    }

    fun onDisablePlayerEyesAnimationChanged(enabled: Boolean) {
        if (state.disablePlayerEyesAnimation == enabled) return
        state = state.copy(
            disablePlayerEyesAnimation = enabled,
            copyState = LaunchArgsCopyState(),
        )
    }

    fun onServerHitmarkerChanged(enabled: Boolean) {
        if (state.serverHitmarker == enabled) return
        state = state.copy(
            serverHitmarker = enabled,
            copyState = LaunchArgsCopyState(),
        )
    }

    fun onOldItemPickupNotificationsChanged(enabled: Boolean) {
        if (state.oldItemPickupNotifications == enabled) return
        state = state.copy(
            oldItemPickupNotifications = enabled,
            copyState = LaunchArgsCopyState(),
        )
    }

    fun onCopyConfirmed() {
        if (launchArgs.isBlank()) return
        state = state.copy(copyState = LaunchArgsCopyState(hasCopied = true))
    }
}
