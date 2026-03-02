package ru.copilot.boost.presentation

class SetupCoordinator(
    private val flowStore: SetupFlowStore,
    private val moduleStores: List<SetupModuleStore>,
) {
    fun openSetupSelection() {
        flowStore.openSetupSelection()
    }

    fun goHome() {
        resetAllModules()
        flowStore.finishToHome()
    }

    fun startFlow(selectedModules: Set<SetupModuleId>): Boolean {
        resetAllModules()
        return flowStore.startFlow(selectedModules = selectedModules)
    }

    fun handleFlowAction(action: SetupFlowAction) {
        flowStore.dispatch(action)
    }

    private fun resetAllModules() {
        moduleStores.forEach { it.reset() }
    }
}
