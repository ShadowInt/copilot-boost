package ru.copilot.boost.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.presentation.ApplyInstructionsStore
import ru.copilot.boost.presentation.SetupModuleId
import ru.copilot.boost.presentation.TweaksInstallMode
import ru.copilot.boost.copyTextToClipboard
import ru.copilot.boost.suppressNextUnloadWarning
import ru.copilot.boost.ui.components.AppSnackbarHost
import ru.copilot.boost.ui.components.ExitConfirmationDialog
import ru.copilot.boost.ui.components.ModuleInstructionCard
import ru.copilot.boost.ui.components.ModuleScaffold
import ru.copilot.boost.ui.components.SnackbarTone
import ru.copilot.boost.ui.components.showAppSnackbar
import ru.copilot.boost.ui.components.stage.SkippedLabel
import ru.copilot.boost.ui.components.stage.StageDefinition
import ru.copilot.boost.ui.components.stage.StageInstructionCard
import ru.copilot.boost.ui.components.stage.StageTitleWithLink

@Composable
fun ApplyInstructionsScreen(
    selectedModules: Set<SetupModuleId>,
    cfgHasChanges: Boolean,
    cfgFileName: String,
    cfgPatchedContent: String,
    onDownloadCfg: () -> Unit,
    launchArgsHasSettings: Boolean,
    launchArgs: String,
    isLaunchArgsCopied: Boolean,
    onCopyLaunchArgs: () -> Unit,
    onBack: () -> Unit,
    onGoHome: () -> Unit,
    applyStore: ApplyInstructionsStore,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showExitConfirmationDialog by remember { mutableStateOf(false) }
    val copiedMessage = stringResource(Res.string.apply_launch_args_copied)
    val tweaksScriptCopiedMessage = stringResource(Res.string.apply_tweaks_script_copied)
    val tweaksInstallScript = remember(cfgFileName, cfgPatchedContent) {
        buildTweaksInstallScript(
            cfgFileName = cfgFileName,
            cfgContent = cfgPatchedContent,
        )
    }
    val tweaksInstallMode = applyStore.tweaksInstallMode
    val isTweaksActivated = when (tweaksInstallMode) {
        TweaksInstallMode.MANUAL -> applyStore.isTweaksDownloadTriggered
        TweaksInstallMode.AUTOMATIC -> applyStore.isTweaksScriptCopied
        null -> false
    }

    val onCopyWithSnackbar = remember(onCopyLaunchArgs, copiedMessage) {
        {
            onCopyLaunchArgs()
            coroutineScope.launch {
                snackbarHostState.showAppSnackbar(
                    message = copiedMessage,
                    tone = SnackbarTone.Success,
                )
            }
            Unit
        }
    }
    val onDownloadCfgWithProgress = remember(onDownloadCfg, applyStore) {
        {
            onDownloadCfg()
            applyStore.onTweaksDownloaded()
            applyStore.updateMaxReachedStageIndex(SetupModuleId.Tweaks, 2)
        }
    }
    val onCopyTweaksScript = remember(tweaksInstallScript, applyStore, tweaksScriptCopiedMessage) {
        {
            copyTextToClipboard(tweaksInstallScript)
            applyStore.onTweaksScriptCopied()
            applyStore.updateMaxReachedStageIndex(SetupModuleId.Tweaks, 2)
            coroutineScope.launch {
                snackbarHostState.showAppSnackbar(
                    message = tweaksScriptCopiedMessage,
                    tone = SnackbarTone.Success,
                )
            }
            Unit
        }
    }

    val tweaksStageList = tweaksStages(
        installMode = tweaksInstallMode,
        script = tweaksInstallScript,
        onInstallModeChanged = { mode -> applyStore.selectTweaksInstallMode(mode) },
        onDownloadCfg = onDownloadCfgWithProgress,
        onCopyScript = onCopyTweaksScript,
    )
    val launchArgsStageList = launchArgsStages(launchArgs, onCopyWithSnackbar)

    val tweaksMaxReachedStage = applyStore.maxReachedStageIndex(SetupModuleId.Tweaks, isTweaksActivated)
    val launchArgsMaxReachedStage = applyStore.maxReachedStageIndex(SetupModuleId.LaunchArgs, isLaunchArgsCopied)
    val incompleteModuleIds = remember(
        selectedModules,
        cfgHasChanges,
        launchArgsHasSettings,
        isTweaksActivated,
        isLaunchArgsCopied,
        tweaksMaxReachedStage,
        launchArgsMaxReachedStage,
        tweaksStageList.size,
    ) {
        buildList {
            if (SetupModuleId.Tweaks in selectedModules && cfgHasChanges) {
                val isCompleted = isTweaksActivated && tweaksMaxReachedStage >= tweaksStageList.size - 1
                if (!isCompleted) add(SetupModuleId.Tweaks)
            }
            if (SetupModuleId.LaunchArgs in selectedModules && launchArgsHasSettings) {
                val isCompleted = isLaunchArgsCopied && launchArgsMaxReachedStage >= launchArgsStageList.size - 1
                if (!isCompleted) add(SetupModuleId.LaunchArgs)
            }
        }
    }

    val onGoHomeWithValidation = remember(incompleteModuleIds, onGoHome) {
        {
            if (incompleteModuleIds.isEmpty()) {
                onGoHome()
            } else {
                showExitConfirmationDialog = true
            }
        }
    }

    ModuleScaffold(
        title = stringResource(Res.string.step_apply_instructions_title),
        onBack = onBack,
        primaryActionText = stringResource(Res.string.apply_go_home),
        onPrimaryAction = onGoHomeWithValidation,
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val visibleModules = remember(selectedModules) {
                    SetupModuleId.entries.filter { it in selectedModules }
                }
                var expandedModuleId by remember(selectedModules) {
                    mutableStateOf(visibleModules.firstOrNull())
                }
                for (moduleId in visibleModules) {
                    val isExpanded = expandedModuleId == moduleId
                    val onToggle = { expandedModuleId = if (expandedModuleId == moduleId) null else moduleId }
                    val cardModifier = if (isExpanded) {
                        Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = true)
                    } else {
                        Modifier.fillMaxWidth()
                    }
                    when (moduleId) {
                        SetupModuleId.Tweaks -> StageInstructionCard(
                            modifier = cardModifier,
                            title = stringResource(Res.string.module_tweaks_title),
                            stages = tweaksStageList,
                            isActivated = isTweaksActivated,
                            hasContent = cfgHasChanges,
                            expanded = isExpanded,
                            onToggle = onToggle,
                            maxReachedStageIndex = tweaksMaxReachedStage,
                            onMaxReachedStageIndexChanged = { index -> applyStore.updateMaxReachedStageIndex(moduleId, index) },
                        )
                        SetupModuleId.LaunchArgs -> StageInstructionCard(
                            modifier = cardModifier,
                            title = stringResource(Res.string.module_launch_args_title),
                            stages = launchArgsStageList,
                            isActivated = isLaunchArgsCopied,
                            expanded = isExpanded,
                            onToggle = onToggle,
                            hasContent = launchArgsHasSettings,
                            maxReachedStageIndex = launchArgsMaxReachedStage,
                            onMaxReachedStageIndexChanged = { index -> applyStore.updateMaxReachedStageIndex(moduleId, index) },
                        )
                        SetupModuleId.Binds -> ModuleInstructionCard(
                            modifier = cardModifier,
                            title = stringResource(Res.string.module_binds_title),
                            expanded = isExpanded,
                            onToggle = onToggle,
                        ) {
                            SkippedLabel()
                        }
                    }
                }
            }

            AppSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            )

            val incompleteModuleTitles = incompleteModuleIds.map { moduleId ->
                when (moduleId) {
                    SetupModuleId.Tweaks -> stringResource(Res.string.module_tweaks_title)
                    SetupModuleId.LaunchArgs -> stringResource(Res.string.module_launch_args_title)
                    SetupModuleId.Binds -> stringResource(Res.string.module_binds_title)
                }
            }
            val incompleteText = incompleteModuleTitles.joinToString(separator = "\n") { "- $it" }
            ExitConfirmationDialog(
                visible = showExitConfirmationDialog,
                title = stringResource(Res.string.apply_exit_dialog_title),
                message = stringResource(Res.string.apply_exit_dialog_message, incompleteText),
                confirmText = stringResource(Res.string.apply_exit_dialog_confirm),
                dismissText = stringResource(Res.string.apply_exit_dialog_dismiss),
                onConfirm = {
                    showExitConfirmationDialog = false
                    onGoHome()
                },
                onDismiss = { showExitConfirmationDialog = false },
            )
        }
    }
}

@Composable
private fun tweaksStages(
    installMode: TweaksInstallMode?,
    script: String,
    onInstallModeChanged: (TweaksInstallMode) -> Unit,
    onDownloadCfg: () -> Unit,
    onCopyScript: () -> Unit,
): List<StageDefinition> = buildList {
    add(StageDefinition(
        text = stringResource(Res.string.apply_tweaks_step_select_mode),
        content = { _ ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InstallModeOption(
                    mode = TweaksInstallMode.MANUAL,
                    selectedMode = installMode,
                    onSelect = { onInstallModeChanged(TweaksInstallMode.MANUAL) },
                    text = stringResource(Res.string.apply_tweaks_mode_manual),
                )
                InstallModeOption(
                    mode = TweaksInstallMode.AUTOMATIC,
                    selectedMode = installMode,
                    onSelect = { onInstallModeChanged(TweaksInstallMode.AUTOMATIC) },
                    text = stringResource(Res.string.apply_tweaks_mode_automatic),
                )
            }
        },
    ))
    if (installMode != null) {
        add(StageDefinition(
            text = if (installMode == TweaksInstallMode.AUTOMATIC) {
                stringResource(Res.string.apply_tweaks_auto_step1)
            } else {
                stringResource(Res.string.apply_tweaks_step1)
            },
            content = { _ ->
                if (installMode == TweaksInstallMode.MANUAL) {
                    Button(
                        onClick = onDownloadCfg,
                        modifier = Modifier.padding(start = 10.dp),
                    ) {
                        Text(stringResource(Res.string.apply_tweaks_download))
                    }
                } else {
                    CopyableCodeBox(
                        text = script,
                        onCopy = onCopyScript,
                    )
                }
            },
        ))
        add(StageDefinition(
            text = if (installMode == TweaksInstallMode.AUTOMATIC) {
                stringResource(Res.string.apply_tweaks_auto_step2)
            } else {
                stringResource(Res.string.apply_tweaks_step2)
            },
        ))
    }
}

@Composable
private fun InstallModeOption(
    mode: TweaksInstallMode,
    selectedMode: TweaksInstallMode?,
    onSelect: () -> Unit,
    text: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selectedMode == mode,
                onClick = onSelect,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = null,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selectedMode == mode,
            onClick = null,
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 10.dp),
        )
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
            CopyableCodeBox(text = launchArgs, onCopy = onCopy)
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
private fun CopyableCodeBox(
    text: String,
    onCopy: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp, max = 180.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(start = 12.dp, top = 6.dp, end = 4.dp, bottom = 6.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 36.dp)
                .verticalScroll(rememberScrollState())
                .align(Alignment.CenterStart),
        )
        IconButton(
            onClick = onCopy,
            enabled = text.isNotBlank(),
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

private fun buildTweaksInstallScript(
    cfgFileName: String,
    cfgContent: String,
): String {
    val base64 = window.btoa(cfgContent)
    return "${'$'}p=Join-Path ${'$'}{env:ProgramFiles(x86)} \"Steam\\steamapps\\common\\Rust\\cfg\\$cfgFileName\";[IO.Directory]::CreateDirectory([IO.Path]::GetDirectoryName(${'$'}p))|Out-Null;[IO.File]::WriteAllBytes(${'$'}p,[Convert]::FromBase64String(\"$base64\"));Write-Host \"Готово: ${'$'}p\" -ForegroundColor Green"
}
