package ru.copilot.boost.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.copilot.boost.presentation.model.LaunchArgsCopyState
import ru.copilot.boost.presentation.model.LaunchArgsUiState

class LaunchArgsStore(
    initialState: LaunchArgsUiState = LaunchArgsUiState(),
) : SetupModuleStore {
    var state by mutableStateOf(initialState)
        private set

    val launchArgs: String
        get() = buildLaunchArgs(state)

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

    override fun reset() {
        state = LaunchArgsUiState()
    }

    companion object {
        fun buildLaunchArgs(state: LaunchArgsUiState): String = buildList {
            if (state.adminTeleport) add("-global.enable_marker_teleport \"True\"")
            if (state.fasterAltHeadTurn) {
                add("-client.headlerp \"10\"")
                add("-headlerp_inertia \"0\"")
            }
            if (state.disablePlayerEyesAnimation) {
                add("-player.eye_blinking \"False\"")
                add("-player.eye_movement \"False\"")
            }
            if (state.serverHitmarker) add("-hitnotify.notification_level \"2\"")
            if (state.oldItemPickupNotifications) {
                add("-global.showitempickupnotices \"1\"")
                add("-global.showitemcountsonpickup \"False\"")
                add("-global.usesingleitempickupnotice \"False\"")
            }
        }.joinToString(" ")
    }
}
