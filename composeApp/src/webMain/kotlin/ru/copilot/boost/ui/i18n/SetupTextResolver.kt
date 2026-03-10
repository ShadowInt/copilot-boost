package ru.copilot.boost.ui.i18n

import androidx.compose.runtime.Composable
import copilotboost.composeapp.generated.resources.Res
import copilotboost.composeapp.generated.resources.module_binds_description
import copilotboost.composeapp.generated.resources.module_binds_title
import copilotboost.composeapp.generated.resources.module_graphics_description
import copilotboost.composeapp.generated.resources.module_graphics_title
import copilotboost.composeapp.generated.resources.module_launch_args_description
import copilotboost.composeapp.generated.resources.module_launch_args_title
import copilotboost.composeapp.generated.resources.module_tweaks_description
import copilotboost.composeapp.generated.resources.module_tweaks_title
import copilotboost.composeapp.generated.resources.step_apply_instructions_title
import copilotboost.composeapp.generated.resources.step_binds_title
import copilotboost.composeapp.generated.resources.step_client_cfg_upload_title
import copilotboost.composeapp.generated.resources.step_graphics_title
import copilotboost.composeapp.generated.resources.step_launch_args_title
import copilotboost.composeapp.generated.resources.step_tweaks_title
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.presentation.SetupTextKey

@Composable
fun setupText(key: SetupTextKey): String = when (key) {
    SetupTextKey.ModuleTweaksTitle -> stringResource(Res.string.module_tweaks_title)
    SetupTextKey.ModuleTweaksDescription -> stringResource(Res.string.module_tweaks_description)
    SetupTextKey.ModuleGraphicsTitle -> stringResource(Res.string.module_graphics_title)
    SetupTextKey.ModuleGraphicsDescription -> stringResource(Res.string.module_graphics_description)
    SetupTextKey.ModuleLaunchArgsTitle -> stringResource(Res.string.module_launch_args_title)
    SetupTextKey.ModuleLaunchArgsDescription -> stringResource(Res.string.module_launch_args_description)
    SetupTextKey.ModuleBindsTitle -> stringResource(Res.string.module_binds_title)
    SetupTextKey.ModuleBindsDescription -> stringResource(Res.string.module_binds_description)
    SetupTextKey.StepClientCfgUploadTitle -> stringResource(Res.string.step_client_cfg_upload_title)
    SetupTextKey.StepTweaksTitle -> stringResource(Res.string.step_tweaks_title)
    SetupTextKey.StepGraphicsTitle -> stringResource(Res.string.step_graphics_title)
    SetupTextKey.StepLaunchArgsTitle -> stringResource(Res.string.step_launch_args_title)
    SetupTextKey.StepBindsTitle -> stringResource(Res.string.step_binds_title)
    SetupTextKey.StepApplyInstructionsTitle -> stringResource(Res.string.step_apply_instructions_title)
}
