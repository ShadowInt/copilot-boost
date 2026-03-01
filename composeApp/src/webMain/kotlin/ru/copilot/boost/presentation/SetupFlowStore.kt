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

    private var setupFlow by mutableStateOf<List<SetupStepDefinition>>(emptyList())
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

    fun startFlow(selectedModules: Set<SetupModuleId>): Boolean {
        val flow = SetupModulesRegistry.buildSteps(selectedModules)
        if (flow.isEmpty()) return false
        setupFlow = flow
        setupFlowStepIndex = 0
        currentScreen = flow.first().screen
        return true
    }

    fun moveForward(): Boolean {
        val nextIndex = setupFlowStepIndex + 1
        if (nextIndex >= setupFlow.size) return false
        setupFlowStepIndex = nextIndex
        currentScreen = setupFlow[nextIndex].screen
        return true
    }

    fun moveBackward(): Boolean {
        val previousIndex = setupFlowStepIndex - 1
        if (previousIndex < 0 || previousIndex >= setupFlow.size) return false
        setupFlowStepIndex = previousIndex
        currentScreen = setupFlow[previousIndex].screen
        return true
    }

    fun hasNextFlowStep(): Boolean = setupFlowStepIndex + 1 < setupFlow.size

    fun isCurrentFlowStep(screen: AppScreen): Boolean = setupFlow.getOrNull(setupFlowStepIndex)?.screen == screen

    fun currentStepTitle(): String? = setupFlow.getOrNull(setupFlowStepIndex)?.title

    fun currentStepRequiresClientCfg(): Boolean = setupFlow.getOrNull(setupFlowStepIndex)?.requiresClientCfg == true

    private fun clearFlow() {
        setupFlow = emptyList()
        setupFlowStepIndex = 0
    }
}
