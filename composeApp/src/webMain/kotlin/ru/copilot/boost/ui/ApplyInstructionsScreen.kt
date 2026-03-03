package ru.copilot.boost.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.*
import kotlinx.browser.window
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.presentation.SetupModuleId
import ru.copilot.boost.suppressNextUnloadWarning
import ru.copilot.boost.ui.components.ModuleInstructionCard
import ru.copilot.boost.ui.components.ModuleScaffold
import ru.copilot.boost.ui.components.stage.StageDefinition
import ru.copilot.boost.ui.components.stage.StageInstructionCard
import ru.copilot.boost.ui.components.stage.StageTitleWithLink

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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val visibleModules = SetupModuleId.entries.filter { it in selectedModules }
            var expandedModuleId by remember(selectedModules) {
                mutableStateOf(visibleModules.firstOrNull())
            }
            for (moduleId in visibleModules) {
                val isExpanded = expandedModuleId == moduleId
                val cardModifier = if (isExpanded) {
                    Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true)
                } else {
                    Modifier.fillMaxWidth()
                }
                when (moduleId) {
                    SetupModuleId.Tweaks -> TwoStepInstructionCard(
                        modifier = cardModifier,
                        title = stringResource(Res.string.module_tweaks_title),
                        hasContent = cfgHasChanges,
                        step1Text = stringResource(Res.string.apply_tweaks_step1),
                        step1ActionLabel = stringResource(Res.string.apply_tweaks_download),
                        onStep1Action = onDownloadCfg,
                        step2Text = stringResource(Res.string.apply_tweaks_step2),
                        expanded = isExpanded,
                        onToggle = {
                            expandedModuleId = if (expandedModuleId == moduleId) null else moduleId
                        },
                    )
                    SetupModuleId.LaunchArgs -> StageInstructionCard(
                        modifier = cardModifier,
                        title = stringResource(Res.string.module_launch_args_title),
                        stages = launchArgsStages(launchArgs, onCopyLaunchArgs),
                        isActivated = isLaunchArgsCopied,
                        expanded = isExpanded,
                        onToggle = {
                            expandedModuleId = if (expandedModuleId == moduleId) null else moduleId
                        },
                        hasContent = launchArgsHasSettings,
                    )
                    SetupModuleId.Binds -> ModuleInstructionCard(
                        modifier = cardModifier,
                        title = stringResource(Res.string.module_binds_title),
                        expanded = isExpanded,
                        onToggle = {
                            expandedModuleId = if (expandedModuleId == moduleId) null else moduleId
                        },
                    ) {
                        Text(
                            text = stringResource(Res.string.apply_skipped),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun launchArgsStages(
    launchArgs: String,
    onCopy: () -> Unit,
): List<StageDefinition> = listOf(
    StageDefinition(
        text = stringResource(Res.string.apply_launch_args_step1),
        content = { _ ->
            LaunchArgsCopyBox(launchArgs = launchArgs, onCopy = onCopy)
        },
    ),
    StageDefinition(
        text = stringResource(Res.string.launch_args_stage_open_steam),
        imageResource = Res.drawable.lib_steam_macos_ru,
        titleContent = { status ->
            StageTitleWithLink(
                text = stringResource(Res.string.launch_args_stage_open_steam),
                linkPhrase = stringResource(Res.string.launch_args_steam_link_phrase),
                onLinkClick = ::openSteamRustDetails,
                status = status,
            )
        },
    ),
    StageDefinition(
        text = stringResource(Res.string.launch_args_stage_paste_params),
        imageResource = Res.drawable.rust_steam_args_macos_ru,
    ),
    StageDefinition(text = stringResource(Res.string.launch_args_stage_done)),
)

private fun openSteamRustDetails() {
    suppressNextUnloadWarning()
    window.location.href = "steam://nav/games/details/252490"
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
    modifier: Modifier = Modifier,
    title: String,
    hasContent: Boolean,
    step1Text: String,
    step1ActionLabel: String,
    onStep1Action: () -> Unit,
    step2Text: String,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    ModuleInstructionCard(
        modifier = modifier,
        title = title,
        expanded = expanded,
        onToggle = onToggle,
    ) {
        if (!hasContent) {
            Text(
                text = stringResource(Res.string.apply_skipped),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
