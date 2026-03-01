package ru.copilot.boost.ui.i18n

import androidx.compose.runtime.Composable
import copilotboost.composeapp.generated.resources.Res
import copilotboost.composeapp.generated.resources.flow_action_finish
import copilotboost.composeapp.generated.resources.flow_action_next
import copilotboost.composeapp.generated.resources.flow_step_counter
import copilotboost.composeapp.generated.resources.flow_unload_warning
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.presentation.SetupPrimaryAction

@Composable
fun flowUnloadWarningText(): String = stringResource(Res.string.flow_unload_warning)

@Composable
fun flowPrimaryActionText(action: SetupPrimaryAction?): String? = when (action) {
    SetupPrimaryAction.Next -> stringResource(Res.string.flow_action_next)
    SetupPrimaryAction.Finish -> stringResource(Res.string.flow_action_finish)
    null -> null
}

@Composable
fun flowStepSubtitle(stepNumber: Int?, totalSteps: Int): String? {
    if (stepNumber == null || totalSteps <= 0) return null
    return stringResource(Res.string.flow_step_counter, stepNumber, totalSteps)
}
