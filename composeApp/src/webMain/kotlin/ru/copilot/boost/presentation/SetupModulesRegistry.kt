package ru.copilot.boost.presentation

import ru.copilot.boost.navigation.AppScreen

enum class SetupModuleId {
    Tweaks,
    LaunchArgs,
    Binds,
}

enum class SetupTextKey {
    ModuleTweaksTitle,
    ModuleTweaksDescription,
    ModuleLaunchArgsTitle,
    ModuleLaunchArgsDescription,
    ModuleBindsTitle,
    ModuleBindsDescription,
    StepClientCfgUploadTitle,
    StepTweaksTitle,
    StepLaunchArgsTitle,
    StepBindsTitle,
}

data class SetupStepDefinition(
    val screen: AppScreen,
    val titleKey: SetupTextKey,
    val canProceed: (SetupFlowContext) -> Boolean = { true },
)

data class SetupFlowContext(
    val hasClientCfg: Boolean,
)

data class SetupModuleDefinition(
    val id: SetupModuleId,
    val titleKey: SetupTextKey,
    val descriptionKey: SetupTextKey,
    val steps: List<SetupStepDefinition>,
)

object SetupModulesRegistry {
    val modules: List<SetupModuleDefinition> = listOf(
        SetupModuleDefinition(
            id = SetupModuleId.Tweaks,
            titleKey = SetupTextKey.ModuleTweaksTitle,
            descriptionKey = SetupTextKey.ModuleTweaksDescription,
            steps = listOf(
                SetupStepDefinition(
                    screen = AppScreen.ClientCfgUpload,
                    titleKey = SetupTextKey.StepClientCfgUploadTitle,
                    canProceed = { context -> context.hasClientCfg },
                ),
                SetupStepDefinition(
                    screen = AppScreen.Tweaks,
                    titleKey = SetupTextKey.StepTweaksTitle,
                ),
            ),
        ),
        SetupModuleDefinition(
            id = SetupModuleId.LaunchArgs,
            titleKey = SetupTextKey.ModuleLaunchArgsTitle,
            descriptionKey = SetupTextKey.ModuleLaunchArgsDescription,
            steps = listOf(
                SetupStepDefinition(
                    screen = AppScreen.LaunchArgs,
                    titleKey = SetupTextKey.StepLaunchArgsTitle,
                ),
            ),
        ),
        SetupModuleDefinition(
            id = SetupModuleId.Binds,
            titleKey = SetupTextKey.ModuleBindsTitle,
            descriptionKey = SetupTextKey.ModuleBindsDescription,
            steps = listOf(
                SetupStepDefinition(
                    screen = AppScreen.Binds,
                    titleKey = SetupTextKey.StepBindsTitle,
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
