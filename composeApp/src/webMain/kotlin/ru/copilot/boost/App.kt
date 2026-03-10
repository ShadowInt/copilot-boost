package ru.copilot.boost

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import ru.copilot.boost.navigation.AppScreen
import ru.copilot.boost.domain.CfgPatcher
import ru.copilot.boost.presentation.ApplyInstructionsStore
import ru.copilot.boost.presentation.CfgEditorStore
import ru.copilot.boost.presentation.GraphicsStore
import ru.copilot.boost.presentation.LaunchArgsStore
import ru.copilot.boost.presentation.SetupCoordinator
import ru.copilot.boost.presentation.SetupFlowAction
import ru.copilot.boost.presentation.SetupFlowContext
import ru.copilot.boost.presentation.SetupFlowStore
import ru.copilot.boost.presentation.SetupFlowUiState
import ru.copilot.boost.presentation.SetupModuleId
import ru.copilot.boost.presentation.SetupModulesRegistry
import ru.copilot.boost.presentation.SetupTextKey
import ru.copilot.boost.presentation.model.CfgUploadError
import copilotboost.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.ui.*
import ru.copilot.boost.ui.components.ModuleScaffold
import ru.copilot.boost.ui.i18n.flowPrimaryActionText
import ru.copilot.boost.ui.i18n.flowStepSubtitle
import ru.copilot.boost.ui.i18n.flowUnloadWarningText
import ru.copilot.boost.ui.i18n.setupText

@Composable
fun App() {
    val cfgStore = remember { CfgEditorStore() }
    val graphicsStore = remember { GraphicsStore() }
    val launchArgsStore = remember { LaunchArgsStore() }
    val applyStore = remember { ApplyInstructionsStore() }
    val cfgPatcher = remember { CfgPatcher() }
    val moduleStores = remember(cfgStore, graphicsStore, launchArgsStore, applyStore) {
        listOf(cfgStore, graphicsStore, launchArgsStore, applyStore)
    }
    val flowStore = remember { SetupFlowStore() }
    val coordinator = remember(flowStore, moduleStores) {
        SetupCoordinator(
            flowStore = flowStore,
            moduleStores = moduleStores,
        )
    }
    val currentScreen = flowStore.currentScreen

    if (currentScreen == AppScreen.ClientCfgUpload) {
        val onFileSelectedAndAdvance: (ru.copilot.boost.model.UploadedFileData) -> Unit = remember(cfgStore, graphicsStore, coordinator) {
            { fileData ->
                cfgStore.onFileSelected(fileData)
                graphicsStore.initWithFile(fileData)
                coordinator.handleFlowAction(SetupFlowAction.Next)
            }
        }

        DisposableEffect(cfgStore) {
            val disposeListeners = observeGlobalFileDrop(
                onDragStateChanged = cfgStore::onDragStateChanged,
                onFileSelected = onFileSelectedAndAdvance,
                onInvalidFile = cfgStore::onInvalidFile,
                onReadError = cfgStore::onReadError,
            )

            onDispose { disposeListeners() }
        }
    }

    val shouldWarnOnPageRefresh = when (currentScreen) {
        AppScreen.Home,
        AppScreen.SetupSelection -> false
        AppScreen.ClientCfgUpload,
        AppScreen.Tweaks,
        AppScreen.Graphics,
        AppScreen.Binds,
        AppScreen.LaunchArgs,
        AppScreen.ApplyInstructions -> true
    }
    val unloadWarningMessage = flowUnloadWarningText()
    if (shouldWarnOnPageRefresh) {
        DisposableEffect(currentScreen) {
            val disposeWarning = observePageUnloadWarning(
                message = unloadWarningMessage,
            )
            onDispose { disposeWarning() }
        }
    }

    LaunchedEffect(cfgStore.state.patchedContent) {
        applyStore.resetTweaksDownload()
    }

    MaterialTheme {
        val flowUiState = flowStore.uiState(
            context = SetupFlowContext(
                hasClientCfg = cfgStore.state.hasFile,
            ),
        )
        val selectionModulesUi = SetupModulesRegistry.modules.map { module ->
            SetupSelectionModuleUi(
                id = module.id,
                title = setupText(module.titleKey),
                description = setupText(module.descriptionKey),
            )
        }

        when (currentScreen) {
            AppScreen.Home -> {
                HomeScreen(
                    appVersion = BuildKonfig.PROJECT_VERSION,
                    onStartSetup = coordinator::openSetupSelection,
                )
            }

            AppScreen.SetupSelection -> {
                SetupSelectionScreen(
                    modules = selectionModulesUi,
                    onStartFlow = coordinator::startFlow,
                    onBackHome = coordinator::goHome,
                )
            }

            AppScreen.ClientCfgUpload -> {
                FlowStepScaffold(
                    flowUiState = flowUiState,
                    defaultTitleKey = SetupTextKey.StepClientCfgUploadTitle,
                    coordinator = coordinator,
                ) {
                    ClientCfgUploadScreen(
                        isDragging = cfgStore.state.isDragging,
                        uploadError = resolveUploadError(cfgStore.state.uploadError),
                        fileName = cfgStore.state.fileName,
                        onPickFileClick = {
                            openFilePicker(
                                onFileSelected = { fileData ->
                                    cfgStore.onFileSelected(fileData)
                                    graphicsStore.initWithFile(fileData)
                                    coordinator.handleFlowAction(SetupFlowAction.Next)
                                },
                                onInvalidFile = cfgStore::onInvalidFile,
                                onReadError = cfgStore::onReadError,
                            )
                        },
                    )
                }
            }

            AppScreen.Tweaks -> {
                FlowStepScaffold(
                    flowUiState = flowUiState,
                    defaultTitleKey = SetupTextKey.StepTweaksTitle,
                    coordinator = coordinator,
                    primaryActionTextOverride = if (!cfgStore.state.hasChanges) {
                        stringResource(Res.string.flow_action_skip)
                    } else {
                        null
                    },
                ) {
                    CfgEditorScreen(
                        state = cfgStore.state,
                        onPresetChanged = cfgStore::onPresetChanged,
                    )
                }
            }

            AppScreen.Graphics -> {
                FlowStepScaffold(
                    flowUiState = flowUiState,
                    defaultTitleKey = SetupTextKey.StepGraphicsTitle,
                    coordinator = coordinator,
                    primaryActionTextOverride = if (!graphicsStore.state.hasChanges) {
                        stringResource(Res.string.flow_action_skip)
                    } else {
                        null
                    },
                ) {
                    GraphicsScreen(
                        state = graphicsStore.state,
                        onSettingChanged = graphicsStore::onSettingChanged,
                        onPresetSelected = graphicsStore::onPresetSelected,
                    )
                }
            }

            AppScreen.Binds -> {
                FlowStepScaffold(
                    flowUiState = flowUiState,
                    defaultTitleKey = SetupTextKey.StepBindsTitle,
                    coordinator = coordinator,
                ) {
                    ModuleStubScreen(
                        title = stringResource(Res.string.binds_stub_title),
                        description = stringResource(Res.string.binds_stub_description),
                    )
                }
            }

            AppScreen.LaunchArgs -> {
                FlowStepScaffold(
                    flowUiState = flowUiState,
                    defaultTitleKey = SetupTextKey.StepLaunchArgsTitle,
                    coordinator = coordinator,
                ) {
                    LaunchArgsScreen(store = launchArgsStore)
                }
            }

            AppScreen.ApplyInstructions -> {
                val finalCfgContent = remember(
                    cfgStore.state.patchedContent,
                    graphicsStore.state.settings,
                    flowStore.selectedModules,
                ) {
                    val base = cfgStore.state.patchedContent.ifEmpty {
                        cfgStore.state.uploadedFile?.content ?: ""
                    }
                    if (SetupModuleId.Graphics in flowStore.selectedModules && graphicsStore.state.hasChanges) {
                        cfgPatcher.applyKeyValues(base, graphicsStore.state.settings.toKeyValues()).updatedContent
                    } else {
                        base
                    }
                }
                val cfgHasAnyChanges = cfgStore.state.hasChanges || graphicsStore.state.hasChanges

                ApplyInstructionsScreen(
                    selectedModules = flowStore.selectedModules,
                    cfgHasChanges = cfgHasAnyChanges,
                    cfgFileName = cfgStore.state.downloadFileName ?: "client.cfg",
                    cfgPatchedContent = finalCfgContent,
                    onDownloadCfg = {
                        val fileName = cfgStore.state.downloadFileName ?: return@ApplyInstructionsScreen
                        downloadCfgFile(
                            fileName = fileName,
                            content = finalCfgContent,
                        )
                    },
                    launchArgsHasSettings = launchArgsStore.hasSelectedSettings,
                    launchArgs = launchArgsStore.launchArgs,
                    isLaunchArgsCopied = launchArgsStore.isCurrentSelectionCopied,
                    onCopyLaunchArgs = {
                        val args = launchArgsStore.launchArgs
                        if (args.isNotBlank()) {
                            copyTextToClipboard(args)
                            launchArgsStore.onCopyConfirmed()
                        }
                    },
                    onBack = coordinator::returnToLastFlowStep,
                    onGoHome = coordinator::goHome,
                    applyStore = applyStore,
                )
            }
        }
    }
}

@Composable
private fun resolveUploadError(error: CfgUploadError?): String? = when (error) {
    CfgUploadError.InvalidFileName -> stringResource(Res.string.error_invalid_file_name)
    CfgUploadError.ReadFailed -> stringResource(Res.string.error_file_read_failed)
    null -> null
}

@Composable
private fun FlowStepScaffold(
    flowUiState: SetupFlowUiState,
    defaultTitleKey: SetupTextKey,
    coordinator: SetupCoordinator,
    primaryActionTextOverride: String? = null,
    content: @Composable () -> Unit,
) {
    val stepSubtitle = flowStepSubtitle(flowUiState.currentStepNumber, flowUiState.totalSteps)
    val primaryActionText = primaryActionTextOverride ?: flowPrimaryActionText(flowUiState.primaryAction)

    val onBack = remember(coordinator) { { coordinator.handleFlowAction(SetupFlowAction.Back) } }
    val onNext = remember(coordinator) { { coordinator.handleFlowAction(SetupFlowAction.Next) } }

    ModuleScaffold(
        title = flowUiState.currentStepTitleKey?.let { setupText(it) } ?: setupText(defaultTitleKey),
        subtitle = stepSubtitle,
        onBack = onBack,
        primaryActionText = primaryActionText,
        primaryActionEnabled = flowUiState.canProceed,
        onPrimaryAction = if (flowUiState.isCurrentStepScreen) onNext else null,
        content = content,
    )
}
