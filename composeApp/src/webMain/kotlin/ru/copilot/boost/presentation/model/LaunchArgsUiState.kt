package ru.copilot.boost.presentation.model

data class LaunchArgsUiState(
    val adminTeleport: Boolean = false,
    val fasterAltHeadTurn: Boolean = false,
    val disablePlayerEyesAnimation: Boolean = false,
    val serverHitmarker: Boolean = false,
    val oldItemPickupNotifications: Boolean = false,
    val copyState: LaunchArgsCopyState = LaunchArgsCopyState(),
)

data class LaunchArgsCopyState(
    val hasCopied: Boolean = false,
    val args: String = "",
)
