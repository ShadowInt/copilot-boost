package ru.copilot.boost

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import ru.copilot.boost.navigation.AppScreen
import ru.copilot.boost.presentation.ApplyInstructionsStore
import ru.copilot.boost.presentation.CfgEditorStore
import ru.copilot.boost.presentation.LaunchArgsStore
import ru.copilot.boost.presentation.SetupCoordinator
import ru.copilot.boost.presentation.SetupFlowAction
import ru.copilot.boost.presentation.SetupFlowContext
import ru.copilot.boost.presentation.SetupFlowStore
import ru.copilot.boost.presentation.SetupFlowUiState
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
    val launchArgsStore = remember { LaunchArgsStore() }
    val applyStore = remember { ApplyInstructionsStore() }
    val moduleStores = remember(cfgStore, launchArgsStore, applyStore) {
        listOf(cfgStore, launchArgsStore, applyStore)
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
        val onFileSelectedAndAdvance: (ru.copilot.boost.model.UploadedFileData) -> Unit = remember(cfgStore, coordinator) {
            { fileData ->
                cfgStore.onFileSelected(fileData)
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

    val shouldWarnOnPageRefresh = currentScreen != AppScreen.Home
    val unloadWarningMessage = flowUnloadWarningText()
    if (shouldWarnOnPageRefresh) {
        DisposableEffect(currentScreen) {
            val disposeWarning = observePageUnloadWarning(
                message = unloadWarningMessage,
            )
            onDispose { disposeWarning() }
        }
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
                ApplyInstructionsScreen(
                    selectedModules = flowStore.selectedModules,
                    cfgHasChanges = cfgStore.state.hasChanges,
                    onDownloadCfg = {
                        val fileName = cfgStore.state.downloadFileName ?: return@ApplyInstructionsScreen
                        downloadCfgFile(
                            fileName = fileName,
                            content = cfgStore.state.patchedContent,
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
