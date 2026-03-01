package ru.copilot.boost

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import ru.copilot.boost.navigation.AppScreen
import ru.copilot.boost.presentation.CfgEditorStore
import ru.copilot.boost.presentation.LaunchArgsStore
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
        val resetAllModules = {
            moduleStores.forEach { it.reset() }
        }
        val resetFlowToHome = {
            resetAllModules()
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
                        resetAllModules()
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
                        cfgStore.state.hasFile
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
                    LaunchArgsScreen(store = launchArgsStore)
                }
            }
        }
    }
}