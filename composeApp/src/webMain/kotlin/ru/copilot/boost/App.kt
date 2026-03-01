package ru.copilot.boost

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.copilot.boost.navigation.AppScreen
import ru.copilot.boost.presentation.CfgEditorStore
import ru.copilot.boost.presentation.SetupFlowStore
import ru.copilot.boost.presentation.SetupModulesRegistry
import ru.copilot.boost.ui.ClientCfgUploadScreen
import ru.copilot.boost.ui.CfgEditorScreen
import ru.copilot.boost.ui.HomeScreen
import ru.copilot.boost.ui.LaunchArgsScreen
import ru.copilot.boost.ui.ModuleStubScreen
import ru.copilot.boost.ui.SetupSelectionScreen
import ru.copilot.boost.ui.components.ModuleScaffold

@Composable
fun App() {
    val store = remember { CfgEditorStore() }
    val flowStore = remember { SetupFlowStore(initialScreen = loadSavedScreen()) }
    val currentScreen = flowStore.currentScreen

    LaunchedEffect(currentScreen) {
        saveScreen(currentScreen)
    }

    if (currentScreen == AppScreen.ClientCfgUpload) {
        DisposableEffect(store) {
            val disposeListeners = observeGlobalFileDrop(
                onDragStateChanged = store::onDragStateChanged,
                onFileSelected = store::onFileSelected,
                onInvalidFile = store::onInvalidFile,
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
        val resetFlowToHome = {
            store.reset()
            flowStore.finishToHome()
        }
        val resetFlowToSelection = {
            flowStore.resetToSelection()
        }

        when (currentScreen) {
            AppScreen.Home -> {
                HomeScreen(
                    appVersion = BuildKonfig.PROJECT_VERSION,
                    onStartSetup = {
                        flowStore.openSetupSelection()
                    },
                )
            }

            AppScreen.SetupSelection -> {
                SetupSelectionScreen(
                    modules = SetupModulesRegistry.modules,
                    onStartFlow = { selectedModules ->
                        store.reset()
                        flowStore.startFlow(selectedModules = selectedModules)
                    },
                    onBackHome = resetFlowToHome,
                )
            }

            AppScreen.ClientCfgUpload -> {
                ModuleScaffold(
                    title = flowStore.currentStepTitle() ?: "Загрузка клиентской конфигурации",
                    onBack = {
                        if (!flowStore.moveBackward()) {
                            resetFlowToSelection()
                        }
                    },
                    primaryActionText = if (flowStore.isCurrentFlowStep(AppScreen.ClientCfgUpload)) "Далее" else null,
                    primaryActionEnabled = if (flowStore.currentStepRequiresClientCfg()) {
                        store.state.hasFile
                    } else {
                        true
                    },
                    onPrimaryAction = if (flowStore.isCurrentFlowStep(AppScreen.ClientCfgUpload)) {
                        {
                            if (!flowStore.moveForward()) {
                                resetFlowToHome()
                            }
                        }
                    } else {
                        null
                    },
                ) {
                    ClientCfgUploadScreen(
                        isDragging = store.state.isDragging,
                        uploadError = store.state.uploadError,
                        fileName = store.state.fileName,
                        onPickFileClick = {
                            openFilePicker(
                                onFileSelected = store::onFileSelected,
                                onInvalidFile = store::onInvalidFile,
                            )
                        },
                    )
                }
            }

            AppScreen.Tweaks -> {
                ModuleScaffold(
                    title = flowStore.currentStepTitle() ?: "Твики",
                    onBack = {
                        if (!flowStore.moveBackward()) {
                            resetFlowToSelection()
                        }
                    },
                    primaryActionText = if (flowStore.isCurrentFlowStep(AppScreen.Tweaks)) {
                        if (flowStore.hasNextFlowStep()) "Далее" else "Завершить"
                    } else {
                        null
                    },
                    onPrimaryAction = if (flowStore.isCurrentFlowStep(AppScreen.Tweaks)) {
                        {
                            if (!flowStore.moveForward()) {
                                resetFlowToHome()
                            }
                        }
                    } else {
                        null
                    },
                ) {
                    val state = store.state
                    CfgEditorScreen(
                        state = state,
                        onDisableParasiticChanged = store::onDisableParasiticChanged,
                        onDisableLegsRenderingChanged = store::onDisableLegsRenderingChanged,
                        onDisableLegsDeformationChanged = store::onDisableLegsDeformationChanged,
                        onDisableStrobeLightsChanged = store::onDisableStrobeLightsChanged,
                        onReduceHeldItemSizeChanged = store::onReduceHeldItemSizeChanged,
                        onRestoreEventTextNotificationsChanged = store::onRestoreEventTextNotificationsChanged,
                        onRemoveAutocraftMenuDelayChanged = store::onRemoveAutocraftMenuDelayChanged,
                        onReduceSleepingBagRemovalDelayChanged = store::onReduceSleepingBagRemovalDelayChanged,
                        onAddMapInfoToF8MenuChanged = store::onAddMapInfoToF8MenuChanged,
                        onDisableClientErrorOverlayChanged = store::onDisableClientErrorOverlayChanged,
                        onAddAdminGesturesToGameMenuChanged = store::onAddAdminGesturesToGameMenuChanged,
                        onConvenientSkinSortingChanged = store::onConvenientSkinSortingChanged,
                        onEnlargedConsoleChanged = store::onEnlargedConsoleChanged,
                        onReduceRadialMenuCallDelayChanged = store::onReduceRadialMenuCallDelayChanged,
                        onLeftHandModeChanged = store::onLeftHandModeChanged,
                        onReduceCameraShakeChanged = store::onReduceCameraShakeChanged,
                        onImproveTreeMarkerVisibilityChanged = store::onImproveTreeMarkerVisibilityChanged,
                        onDisableOcclusionCullingSafeModeChanged = store::onDisableOcclusionCullingSafeModeChanged,
                        onDisableGibsCompletelyChanged = store::onDisableGibsCompletelyChanged,
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
                    title = flowStore.currentStepTitle() ?: "Бинды",
                    onBack = {
                        if (!flowStore.moveBackward()) {
                            resetFlowToSelection()
                        }
                    },
                    primaryActionText = if (flowStore.isCurrentFlowStep(AppScreen.Binds)) "Завершить" else null,
                    onPrimaryAction = if (flowStore.isCurrentFlowStep(AppScreen.Binds)) {
                        resetFlowToHome
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
                    title = flowStore.currentStepTitle() ?: "Параметры запуска",
                    onBack = {
                        if (!flowStore.moveBackward()) {
                            resetFlowToSelection()
                        }
                    },
                    primaryActionText = if (flowStore.isCurrentFlowStep(AppScreen.LaunchArgs)) {
                        if (flowStore.hasNextFlowStep()) "Далее" else "Завершить"
                    } else {
                        null
                    },
                    onPrimaryAction = if (flowStore.isCurrentFlowStep(AppScreen.LaunchArgs)) {
                        {
                            if (!flowStore.moveForward()) {
                                resetFlowToHome()
                            }
                        }
                    } else {
                        null
                    },
                ) {
                    LaunchArgsScreen()
                }
            }
        }
    }
}