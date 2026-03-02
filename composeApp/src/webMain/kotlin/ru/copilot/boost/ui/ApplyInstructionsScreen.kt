package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.presentation.SetupModuleId

@Composable
fun ApplyInstructionsScreen(
    selectedModules: Set<SetupModuleId>,
    cfgHasChanges: Boolean,
    onDownloadCfg: () -> Unit,
    launchArgsHasSettings: Boolean,
    launchArgs: String,
    onCopyLaunchArgs: () -> Unit,
    onGoHome: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.step_apply_instructions_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val moduleOrder = SetupModuleId.entries
            for (moduleId in moduleOrder) {
                if (moduleId !in selectedModules) continue
                when (moduleId) {
                    SetupModuleId.Tweaks -> TwoStepInstructionCard(
                        title = stringResource(Res.string.module_tweaks_title),
                        hasContent = cfgHasChanges,
                        step1Text = stringResource(Res.string.apply_tweaks_step1),
                        step1ActionLabel = stringResource(Res.string.apply_tweaks_download),
                        onStep1Action = onDownloadCfg,
                        step2Text = stringResource(Res.string.apply_tweaks_step2),
                    )
                    SetupModuleId.LaunchArgs -> TwoStepInstructionCard(
                        title = stringResource(Res.string.module_launch_args_title),
                        hasContent = launchArgsHasSettings,
                        step1Text = stringResource(Res.string.apply_launch_args_step1),
                        step1ActionLabel = stringResource(Res.string.apply_launch_args_copy),
                        onStep1Action = onCopyLaunchArgs,
                        step2Text = stringResource(Res.string.apply_launch_args_step2),
                    )
                    SetupModuleId.Binds -> ModuleInstructionCard(
                        title = stringResource(Res.string.module_binds_title),
                    ) { SkippedLabel() }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onGoHome) {
            Text(stringResource(Res.string.apply_go_home))
        }
    }
}

@Composable
private fun TwoStepInstructionCard(
    title: String,
    hasContent: Boolean,
    step1Text: String,
    step1ActionLabel: String,
    onStep1Action: () -> Unit,
    step2Text: String,
) {
    ModuleInstructionCard(title = title) {
        if (!hasContent) {
            SkippedLabel()
        } else {
            InstructionStepWithAction(
                stepNumber = 1,
                text = step1Text,
                actionLabel = step1ActionLabel,
                onAction = onStep1Action,
            )
            Spacer(modifier = Modifier.height(8.dp))
            InstructionStep(
                stepNumber = 2,
                text = step2Text,
            )
        }
    }
}

@Composable
private fun ModuleInstructionCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun InstructionStep(stepNumber: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "$stepNumber.",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun InstructionStepWithAction(
    stepNumber: Int,
    text: String,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = "$stepNumber.",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Button(
            onClick = onAction,
            modifier = Modifier.padding(start = 12.dp),
        ) {
            Text(actionLabel)
        }
    }
}

@Composable
private fun SkippedLabel() {
    Text(
        text = stringResource(Res.string.apply_skipped),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
