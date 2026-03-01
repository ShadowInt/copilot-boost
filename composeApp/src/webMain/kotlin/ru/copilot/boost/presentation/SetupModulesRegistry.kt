package ru.copilot.boost.presentation

import ru.copilot.boost.navigation.AppScreen

enum class SetupModuleId {
    Tweaks,
    LaunchArgs,
    Binds,
}

data class SetupStepDefinition(
    val screen: AppScreen,
    val title: String,
    val requiresClientCfg: Boolean = false,
)

data class SetupModuleDefinition(
    val id: SetupModuleId,
    val title: String,
    val description: String,
    val steps: List<SetupStepDefinition>,
)

object SetupModulesRegistry {
    val modules: List<SetupModuleDefinition> = listOf(
        SetupModuleDefinition(
            id = SetupModuleId.Tweaks,
            title = "Твики",
            description = "Понадобится файл client.cfg через drag-and-drop",
            steps = listOf(
                SetupStepDefinition(
                    screen = AppScreen.ClientCfgUpload,
                    title = "Загрузка клиентской конфигурации",
                    requiresClientCfg = true,
                ),
                SetupStepDefinition(
                    screen = AppScreen.Tweaks,
                    title = "Твики",
                ),
            ),
        ),
        SetupModuleDefinition(
            id = SetupModuleId.LaunchArgs,
            title = "Параметры запуска",
            description = "Настройка аргументов запуска клиента",
            steps = listOf(
                SetupStepDefinition(
                    screen = AppScreen.LaunchArgs,
                    title = "Параметры запуска",
                ),
            ),
        ),
        SetupModuleDefinition(
            id = SetupModuleId.Binds,
            title = "Бинды",
            description = "Настройка и управление биндами",
            steps = listOf(
                SetupStepDefinition(
                    screen = AppScreen.Binds,
                    title = "Бинды",
                ),
            ),
        ),
    )

    fun buildSteps(selectedModules: Set<SetupModuleId>): List<SetupStepDefinition> {
        return modules
            .filter { it.id in selectedModules }
            .flatMap { it.steps }
    }
}
