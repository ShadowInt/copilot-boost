package ru.copilot.boost.domain.model

data class AppliedPresetState(
    val disableParasiticParameters: Boolean,
    val disableLegsRendering: Boolean,
    val disableLegsDeformation: Boolean,
    val disableStrobeLights: Boolean,
    val reduceHeldItemSize: Boolean,
    val restoreEventTextNotifications: Boolean,
    val removeAutocraftMenuDelay: Boolean,
    val reduceSleepingBagRemovalDelay: Boolean,
    val addMapInfoToF8Menu: Boolean,
    val disableClientErrorOverlay: Boolean,
    val reduceCameraShake: Boolean,
    val improveTreeMarkerVisibility: Boolean,
    val disableOcclusionCullingSafeMode: Boolean,
    val disableGibsCompletely: Boolean,
)
