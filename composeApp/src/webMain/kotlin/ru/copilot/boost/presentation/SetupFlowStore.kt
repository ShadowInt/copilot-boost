package ru.copilot.boost.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.copilot.boost.navigation.AppScreen

class SetupFlowStore(
    initialScreen: AppScreen = AppScreen.Home,
) {
    var currentScreen by mutableStateOf(initialScreen)
        private set

    private var setupFlow by mutableStateOf<List<AppScreen>>(emptyList())
    private var setupFlowStepIndex by mutableIntStateOf(0)

    fun openSetupSelection() {
        clearFlow()
        currentScreen = AppScreen.SetupSelection
    }

    fun finishToHome() {
        clearFlow()
        currentScreen = AppScreen.Home
    }

    fun resetToSelection() {
        clearFlow()
        currentScreen = AppScreen.SetupSelection
    }

    fun startFlow(
        includeTweaks: Boolean,
        includeLaunchArgs: Boolean,
        includeBinds: Boolean,
    ): Boolean {
        val flow = buildSetupFlow(
            includeTweaks = includeTweaks,
            includeLaunchArgs = includeLaunchArgs,
            includeBinds = includeBinds,
        )
        if (flow.isEmpty()) return false
        setupFlow = flow
        setupFlowStepIndex = 0
        currentScreen = flow.first()
        return true
    }

    fun moveForward(): Boolean {
        val nextIndex = setupFlowStepIndex + 1
        if (nextIndex >= setupFlow.size) return false
        setupFlowStepIndex = nextIndex
        currentScreen = setupFlow[nextIndex]
        return true
    }

    fun moveBackward(): Boolean {
        val previousIndex = setupFlowStepIndex - 1
        if (previousIndex < 0 || previousIndex >= setupFlow.size) return false
        setupFlowStepIndex = previousIndex
        currentScreen = setupFlow[previousIndex]
        return true
    }

    fun hasNextFlowStep(): Boolean = setupFlowStepIndex + 1 < setupFlow.size

    fun isCurrentFlowStep(screen: AppScreen): Boolean = setupFlow.getOrNull(setupFlowStepIndex) == screen

    private fun clearFlow() {
        setupFlow = emptyList()
        setupFlowStepIndex = 0
    }

    private fun buildSetupFlow(
        includeTweaks: Boolean,
        includeLaunchArgs: Boolean,
        includeBinds: Boolean,
    ): List<AppScreen> {
        val flow = mutableListOf<AppScreen>()
        if (includeTweaks) {
            flow += AppScreen.ClientCfgUpload
            flow += AppScreen.Tweaks
        }
        if (includeLaunchArgs) flow += AppScreen.LaunchArgs
        if (includeBinds) flow += AppScreen.Binds
        return flow
    }
}
