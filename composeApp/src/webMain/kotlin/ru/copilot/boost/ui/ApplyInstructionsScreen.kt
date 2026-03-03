package ru.copilot.boost.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.*
import kotlinx.browser.window
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.presentation.SetupModuleId
import ru.copilot.boost.suppressNextUnloadWarning
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
                    SetupModuleId.LaunchArgs -> LaunchArgsInstructionCard(
                        modifier = cardModifier,
                        hasSettings = launchArgsHasSettings,
                        launchArgs = launchArgs,
                        isCopied = isLaunchArgsCopied,
                        onCopy = onCopyLaunchArgs,
                        expanded = isExpanded,
                        onToggle = {
                            expandedModuleId = if (expandedModuleId == moduleId) null else moduleId
                        },
                    )
                    SetupModuleId.Binds -> ModuleInstructionCard(
                        modifier = cardModifier,
                        title = stringResource(Res.string.module_binds_title),
                        expanded = isExpanded,
                        onToggle = {
                            expandedModuleId = if (expandedModuleId == moduleId) null else moduleId
                        },
                    ) { SkippedLabel() }
                }
            }
        }
    }
}

@Composable
private fun LaunchArgsInstructionCard(
    modifier: Modifier = Modifier,
    hasSettings: Boolean,
    launchArgs: String,
    isCopied: Boolean,
    onCopy: () -> Unit,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
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
            titleContent = { status ->
                OpenSteamTitleWithLink(status = status)
            },
        ),
        StageDefinition(
            text = stringResource(Res.string.launch_args_stage_paste_params),
            imageResource = Res.drawable.rust_steam_args_macos_ru,
        ),
        StageDefinition(text = stringResource(Res.string.launch_args_stage_done)),
    )
    val totalStages = stages.size
    var maxReachedStageIndex by remember(isCopied, totalStages) {
        mutableIntStateOf(if (isCopied) 1 else 0)
    }
    ModuleInstructionCard(
        modifier = modifier,
        title = stringResource(Res.string.module_launch_args_title),
        expanded = expanded,
        onToggle = onToggle,
        statusIcon = if (isCopied && maxReachedStageIndex >= (totalStages - 1)) {
            Icons.Filled.CheckCircle
        } else {
            Icons.Filled.Schedule
        },
        statusIconTint = if (isCopied && maxReachedStageIndex >= (totalStages - 1)) {
            Color(0xFF2E7D32)
        } else {
            Color(0xFFF9A825)
        },
        statusContentDescription = if (isCopied && maxReachedStageIndex >= (totalStages - 1)) {
            stringResource(Res.string.apply_status_completed)
        } else {
            stringResource(Res.string.apply_status_in_progress)
        },
    ) {
        if (!hasSettings) {
            SkippedLabel()
            return@ModuleInstructionCard
        }

        val scrollState = rememberScrollState()

        val scrollValue = scrollState.value
        val maxScrollValue = scrollState.maxValue
        val activeStageIndex = if (maxScrollValue > 0) {
            ((scrollValue.toFloat() / maxScrollValue) * totalStages)
                .toInt()
                .coerceIn(0, totalStages - 1)
        } else {
            0
        }
        val isAtBottom = maxScrollValue > 0 && scrollValue >= (maxScrollValue - 24)
        val currentReachedStage = when {
            !isCopied -> 0
            isAtBottom -> totalStages - 1
            else -> activeStageIndex.coerceAtLeast(1)
        }
        if (currentReachedStage > maxReachedStageIndex) {
            maxReachedStageIndex = currentReachedStage
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
        ) {
            key(maxReachedStageIndex, isCopied) {
                StageTimeline(
                    stages = stages,
                    stageStatusProvider = { index ->
                        launchArgsStageStatus(
                            index = index,
                            isCopied = isCopied,
                            maxReachedStageIndex = maxReachedStageIndex,
                            lastStageIndex = totalStages - 1,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun OpenSteamTitleWithLink(status: StageStatus) {
    val baseText = stringResource(Res.string.launch_args_stage_open_steam)
    val labelColor = when (status) {
        StageStatus.NOT_STARTED -> MaterialTheme.colorScheme.onSurfaceVariant
        StageStatus.IN_PROGRESS -> MaterialTheme.colorScheme.onSurface
        StageStatus.COMPLETED -> MaterialTheme.colorScheme.onSurface
    }
    val linkColor = MaterialTheme.colorScheme.primary
    val linkPhrase = "Перейти к свойствам Rust в Steam"
    val linkStart = baseText.indexOf(linkPhrase)
    val linkEnd = if (linkStart >= 0) linkStart + linkPhrase.length else -1
    val annotated = remember(baseText, linkStart, linkEnd, linkColor) {
        buildAnnotatedString {
            if (linkStart in 0..<linkEnd) {
                append(baseText.substring(0, linkStart))
                withLink(
                    LinkAnnotation.Clickable(
                        tag = "steam-link",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline,
                            ),
                        ),
                        linkInteractionListener = {
                            openSteamRustDetails()
                        },
                    ),
                ) {
                    append(linkPhrase)
                }
                append(baseText.substring(linkEnd))
            } else {
                append(baseText)
            }
        }
    }
    Text(
        text = annotated,
        modifier = Modifier.padding(start = 10.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
        style = MaterialTheme.typography.bodyMedium.copy(color = labelColor),
    )
}

private fun launchArgsStageStatus(
    index: Int,
    isCopied: Boolean,
    maxReachedStageIndex: Int,
    lastStageIndex: Int,
): StageStatus {
    return when {
        !isCopied && index == 0 -> StageStatus.IN_PROGRESS
        !isCopied -> StageStatus.NOT_STARTED
        maxReachedStageIndex >= lastStageIndex -> StageStatus.COMPLETED
        index < maxReachedStageIndex -> StageStatus.COMPLETED
        index == maxReachedStageIndex -> StageStatus.IN_PROGRESS
        else -> StageStatus.NOT_STARTED
    }
}

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
    modifier: Modifier = Modifier,
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    statusIcon: ImageVector? = null,
    statusIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    statusContentDescription: String? = null,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle,
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (statusIcon != null) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = statusContentDescription,
                            tint = statusIconTint,
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                    )
                }
            }
            if (!expanded) return@Column
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true),
            ) {
                content()
            }
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
