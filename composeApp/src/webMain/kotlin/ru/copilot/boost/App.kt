package ru.copilot.boost

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import ru.copilot.boost.navigation.AppScreen
import ru.copilot.boost.presentation.CfgEditorStore
import ru.copilot.boost.presentation.LaunchArgsStore
import ru.copilot.boost.presentation.SetupCoordinator
import ru.copilot.boost.presentation.SetupFlowAction
import ru.copilot.boost.presentation.SetupFlowContext
import ru.copilot.boost.presentation.SetupFlowStore
import ru.copilot.boost.presentation.SetupModulesRegistry
import ru.copilot.boost.ui.*
import ru.copilot.boost.ui.components.ModuleScaffold

@Composable
fun App() {
    val cfgStore = remember { CfgEditorStore() }
    val launchArgsStore = remember { LaunchArgsStore() }
    val moduleStores = remember(cfgStore, launchArgsStore) {
        listOf(cfgStore, launchArgsStore)
    }
    val flowStore = remember { SetupFlowStore(initialScreen = loadSavedScreen()) }
    val coordinator = remember(flowStore, moduleStores) {
        SetupCoordinator(
            flowStore = flowStore,
            moduleStores = moduleStores,
        )
    }
    val currentScreen = flowStore.currentScreen

    LaunchedEffect(currentScreen) {
        saveScreen(currentScreen)
    }

    if (currentScreen == AppScreen.ClientCfgUpload) {
        DisposableEffect(cfgStore) {
            val disposeListeners = observeGlobalFileDrop(
                onDragStateChanged = cfgStore::onDragStateChanged,
                onFileSelected = cfgStore::onFileSelected,
                onInvalidFile = cfgStore::onInvalidFile,
            )

            onDispose { disposeListeners() }
        }
    }

    val shouldWarnOnPageRefresh = currentScreen != AppScreen.Home
    if (shouldWarnOnPageRefresh) {
        DisposableEffect(currentScreen) {
            val disposeWarning = observePageUnloadWarning(
                message = "Прогресс настройки будет потерян. Продолжить?",
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
        val stepSubtitle = flowUiState.currentStepNumber?.let { step ->
            if (flowUiState.totalSteps > 0) "ШАГ $step/${flowUiState.totalSteps}" else null
        }

        when (currentScreen) {
            AppScreen.Home -> {
                HomeScreen(
                    appVersion = BuildKonfig.PROJECT_VERSION,
                    onStartSetup = {
                        coordinator.openSetupSelection()
                    },
                )
            }

            AppScreen.SetupSelection -> {
                SetupSelectionScreen(
                    modules = SetupModulesRegistry.modules,
                    onStartFlow = { selectedModules ->
                        coordinator.startFlow(selectedModules = selectedModules)
                    },
                    onBackHome = coordinator::goHome,
                )
            }

            AppScreen.ClientCfgUpload -> {
                ModuleScaffold(
                    title = flowUiState.currentStepTitle ?: "Загрузка клиентской конфигурации",
                    subtitle = stepSubtitle,
                    onBack = {
                        coordinator.handleFlowAction(SetupFlowAction.Back)
                    },
                    primaryActionText = if (flowUiState.isCurrentStepScreen) {
                        if (flowUiState.hasNextStep) "Далее" else "Завершить"
                    } else {
                        null
                    },
                    primaryActionEnabled = flowUiState.canProceed,
                    onPrimaryAction = if (flowUiState.isCurrentStepScreen) {
                        { coordinator.handleFlowAction(SetupFlowAction.Next) }
                    } else {
                        null
                    },
                ) {
                    ClientCfgUploadScreen(
                        isDragging = cfgStore.state.isDragging,
                        uploadError = cfgStore.state.uploadError,
                        fileName = cfgStore.state.fileName,
                        onPickFileClick = {
                            openFilePicker(
                                onFileSelected = cfgStore::onFileSelected,
                                onInvalidFile = cfgStore::onInvalidFile,
                            )
                        },
                    )
                }
            }

            AppScreen.Tweaks -> {
                ModuleScaffold(
                    title = flowUiState.currentStepTitle ?: "Твики",
                    subtitle = stepSubtitle,
                    onBack = {
                        coordinator.handleFlowAction(SetupFlowAction.Back)
                    },
                    primaryActionText = if (flowUiState.isCurrentStepScreen) {
                        if (flowUiState.hasNextStep) "Далее" else "Завершить"
                    } else {
                        null
                    },
                    primaryActionEnabled = flowUiState.canProceed,
                    onPrimaryAction = if (flowUiState.isCurrentStepScreen) {
                        { coordinator.handleFlowAction(SetupFlowAction.Next) }
                    } else {
                        null
                    },
                ) {
                    val state = cfgStore.state
                    CfgEditorScreen(
                        state = state,
                        onDisableParasiticChanged = cfgStore::onDisableParasiticChanged,
                        onDisableLegsRenderingChanged = cfgStore::onDisableLegsRenderingChanged,
                        onDisableLegsDeformationChanged = cfgStore::onDisableLegsDeformationChanged,
                        onDisableStrobeLightsChanged = cfgStore::onDisableStrobeLightsChanged,
                        onReduceHeldItemSizeChanged = cfgStore::onReduceHeldItemSizeChanged,
                        onRestoreEventTextNotificationsChanged = cfgStore::onRestoreEventTextNotificationsChanged,
                        onRemoveAutocraftMenuDelayChanged = cfgStore::onRemoveAutocraftMenuDelayChanged,
                        onReduceSleepingBagRemovalDelayChanged = cfgStore::onReduceSleepingBagRemovalDelayChanged,
                        onAddMapInfoToF8MenuChanged = cfgStore::onAddMapInfoToF8MenuChanged,
                        onDisableClientErrorOverlayChanged = cfgStore::onDisableClientErrorOverlayChanged,
                        onAddAdminGesturesToGameMenuChanged = cfgStore::onAddAdminGesturesToGameMenuChanged,
                        onConvenientSkinSortingChanged = cfgStore::onConvenientSkinSortingChanged,
                        onEnlargedConsoleChanged = cfgStore::onEnlargedConsoleChanged,
                        onReduceRadialMenuCallDelayChanged = cfgStore::onReduceRadialMenuCallDelayChanged,
                        onLeftHandModeChanged = cfgStore::onLeftHandModeChanged,
                        onReduceCameraShakeChanged = cfgStore::onReduceCameraShakeChanged,
                        onImproveTreeMarkerVisibilityChanged = cfgStore::onImproveTreeMarkerVisibilityChanged,
                        onDisableOcclusionCullingSafeModeChanged = cfgStore::onDisableOcclusionCullingSafeModeChanged,
                        onDisableGibsCompletelyChanged = cfgStore::onDisableGibsCompletelyChanged,
                        onDownloadClick = {
                            val fileName = state.downloadFileName ?: return@CfgEditorScreen
                            downloadCfgFile(
                                fileName = fileName,
                                content = state.patchedContent,
                            )
                        },
                    )
                }
            }

            AppScreen.Binds -> {
                ModuleScaffold(
                    title = flowUiState.currentStepTitle ?: "Бинды",
                    subtitle = stepSubtitle,
                    onBack = {
                        coordinator.handleFlowAction(SetupFlowAction.Back)
                    },
                    primaryActionText = if (flowUiState.isCurrentStepScreen) {
                        if (flowUiState.hasNextStep) "Далее" else "Завершить"
                    } else {
                        null
                    },
                    primaryActionEnabled = flowUiState.canProceed,
                    onPrimaryAction = if (flowUiState.isCurrentStepScreen) {
                        { coordinator.handleFlowAction(SetupFlowAction.Next) }
                    } else {
                        null
                    },
                ) {
                    ModuleStubScreen(
                        title = "Функционал биндов",
                        description = "Здесь будет настройка биндов и пресетов клавиш.",
                    )
                }
            }

            AppScreen.LaunchArgs -> {
                ModuleScaffold(
                    title = flowUiState.currentStepTitle ?: "Параметры запуска",
                    subtitle = stepSubtitle,
                    onBack = {
                        coordinator.handleFlowAction(SetupFlowAction.Back)
                    },
                    primaryActionText = if (flowUiState.isCurrentStepScreen) {
                        if (flowUiState.hasNextStep) "Далее" else "Завершить"
                    } else {
                        null
                    },
                    primaryActionEnabled = flowUiState.canProceed,
                    onPrimaryAction = if (flowUiState.isCurrentStepScreen) {
                        { coordinator.handleFlowAction(SetupFlowAction.Next) }
                    } else {
                        null
                    },
                ) {
                    LaunchArgsScreen(store = launchArgsStore)
                }
            }
        }
    }
}