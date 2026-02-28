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
        get() = hasSelectedSettings &&
            state.copyState.hasCopied &&
            state.copyState.args == launchArgs

    fun onAdminTeleportChanged(enabled: Boolean) {
        if (state.adminTeleport == enabled) return
        state = state.copy(adminTeleport = enabled)
    }

    fun onFasterAltHeadTurnChanged(enabled: Boolean) {
        if (state.fasterAltHeadTurn == enabled) return
        state = state.copy(fasterAltHeadTurn = enabled)
    }

    fun onDisablePlayerEyesAnimationChanged(enabled: Boolean) {
        if (state.disablePlayerEyesAnimation == enabled) return
        state = state.copy(disablePlayerEyesAnimation = enabled)
    }

    fun onServerHitmarkerChanged(enabled: Boolean) {
        if (state.serverHitmarker == enabled) return
        state = state.copy(serverHitmarker = enabled)
    }

    fun onOldItemPickupNotificationsChanged(enabled: Boolean) {
        if (state.oldItemPickupNotifications == enabled) return
        state = state.copy(oldItemPickupNotifications = enabled)
    }

    fun onCopyConfirmed() {
        val args = launchArgs
        if (args.isBlank()) return
        state = state.copy(copyState = LaunchArgsCopyState(hasCopied = true, args = args))
    }
}
