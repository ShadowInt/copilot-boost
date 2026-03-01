package ru.copilot.boost.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.copilot.boost.navigation.AppScreen

enum class SetupFlowAction {
    Back,
    Next,
}

data class SetupFlowUiState(
    val currentScreen: AppScreen,
    val currentStepTitle: String? = null,
    val currentStepNumber: Int? = null,
    val totalSteps: Int = 0,
    val isCurrentStepScreen: Boolean = false,
    val hasNextStep: Boolean = false,
    val canProceed: Boolean = true,
)

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

    fun dispatch(action: SetupFlowAction): AppScreen {
        return when (action) {
            SetupFlowAction.Back -> goBackOrSelection()
            SetupFlowAction.Next -> goNextOrFinish()
        }
    }

    fun goNextOrFinish(): AppScreen {
        val moved = moveForward()
        if (!moved) {
            finishToHome()
        }
        return currentScreen
    }

    fun goBackOrSelection(): AppScreen {
        val moved = moveBackward()
        if (!moved) {
            resetToSelection()
        }
        return currentScreen
    }

    private fun moveForward(): Boolean {
        val nextIndex = setupFlowStepIndex + 1
        if (nextIndex >= setupFlow.size) return false
        setupFlowStepIndex = nextIndex
        currentScreen = setupFlow[nextIndex].screen
        return true
    }

    private fun moveBackward(): Boolean {
        val previousIndex = setupFlowStepIndex - 1
        if (previousIndex < 0 || previousIndex >= setupFlow.size) return false
        setupFlowStepIndex = previousIndex
        currentScreen = setupFlow[previousIndex].screen
        return true
    }

    fun hasNextFlowStep(): Boolean = setupFlowStepIndex + 1 < setupFlow.size

    fun uiState(context: SetupFlowContext): SetupFlowUiState {
        val step = setupFlow.getOrNull(setupFlowStepIndex)
        return SetupFlowUiState(
            currentScreen = currentScreen,
            currentStepTitle = step?.title,
            currentStepNumber = if (step != null) setupFlowStepIndex + 1 else null,
            totalSteps = setupFlow.size,
            isCurrentStepScreen = step?.screen == currentScreen,
            hasNextStep = hasNextFlowStep(),
            canProceed = step?.canProceed?.invoke(context) ?: true,
        )
    }

    private fun clearFlow() {
        setupFlow = emptyList()
        setupFlowStepIndex = 0
    }
}
