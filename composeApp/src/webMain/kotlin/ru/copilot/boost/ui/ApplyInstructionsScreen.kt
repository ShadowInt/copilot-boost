package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.presentation.SetupModuleId
import ru.copilot.boost.ui.components.ModuleScaffold
import ru.copilot.boost.ui.components.stage.StageDefinition
import ru.copilot.boost.ui.components.stage.StageStatus
import ru.copilot.boost.ui.components.stage.StageTimeline

@Composable
fun ApplyInstructionsScreen(
    selectedModules: Set<SetupModuleId>,
    cfgHasChanges: Boolean,
    onDownloadCfg: () -> Unit,
    launchArgsHasSettings: Boolean,
    launchArgs: String,
    isLaunchArgsCopied: Boolean,
    onCopyLaunchArgs: () -> Unit,
    onBack: () -> Unit,
    onGoHome: () -> Unit,
) {
    ModuleScaffold(
        title = stringResource(Res.string.step_apply_instructions_title),
        onBack = onBack,
        primaryActionText = stringResource(Res.string.apply_go_home),
        onPrimaryAction = onGoHome,
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize()
                .padding(24.dp)
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
                    SetupModuleId.LaunchArgs -> LaunchArgsInstructionCard(
                        hasSettings = launchArgsHasSettings,
                        launchArgs = launchArgs,
                        isCopied = isLaunchArgsCopied,
                        onCopy = onCopyLaunchArgs,
                    )
                    SetupModuleId.Binds -> ModuleInstructionCard(
                        title = stringResource(Res.string.module_binds_title),
                    ) { SkippedLabel() }
                }
            }
        }
    }
}

@Composable
private fun LaunchArgsInstructionCard(
    hasSettings: Boolean,
    launchArgs: String,
    isCopied: Boolean,
    onCopy: () -> Unit,
) {
    ModuleInstructionCard(title = stringResource(Res.string.module_launch_args_title)) {
        if (!hasSettings) {
            SkippedLabel()
            return@ModuleInstructionCard
        }

        val stages = listOf(
            StageDefinition(
                text = stringResource(Res.string.apply_launch_args_step1),
                content = { _ ->
                    LaunchArgsCopyBox(launchArgs = launchArgs, onCopy = onCopy)
                },
            ),
            StageDefinition(
                text = stringResource(Res.string.launch_args_stage_open_steam),
                imageResource = Res.drawable.lib_steam_macos_ru,
            ),
            StageDefinition(
                text = stringResource(Res.string.launch_args_stage_paste_params),
                imageResource = Res.drawable.rust_steam_args_macos_ru,
            ),
            StageDefinition(text = stringResource(Res.string.launch_args_stage_done)),
        )

        StageTimeline(
            stages = stages,
            stageStatusProvider = { index ->
                launchArgsStageStatus(index, isCopied)
            },
        )
    }
}

private fun launchArgsStageStatus(index: Int, isCopied: Boolean): StageStatus = when {
    index == 0 && isCopied -> StageStatus.COMPLETED
    index == 0 -> StageStatus.IN_PROGRESS
    isCopied -> StageStatus.IN_PROGRESS
    else -> StageStatus.NOT_STARTED
}

@Composable
private fun LaunchArgsCopyBox(
    launchArgs: String,
    onCopy: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp, max = 180.dp)
            .border(1.dp, Color.Gray)
            .padding(start = 12.dp, top = 6.dp, end = 4.dp, bottom = 6.dp),
    ) {
        Text(
            text = launchArgs,
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 36.dp)
                .verticalScroll(rememberScrollState())
                .align(Alignment.CenterStart),
        )
        IconButton(
            onClick = onCopy,
            enabled = launchArgs.isNotBlank(),
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterEnd),
        ) {
            Icon(
                imageVector = Icons.Filled.ContentCopy,
                contentDescription = stringResource(Res.string.apply_launch_args_copy),
            )
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
