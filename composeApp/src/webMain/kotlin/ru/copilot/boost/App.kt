package ru.copilot.boost

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.copilot.boost.navigation.AppScreen
import ru.copilot.boost.presentation.CfgEditorStore
import ru.copilot.boost.presentation.SetupFlowStore
import ru.copilot.boost.ui.ClientCfgUploadScreen
import ru.copilot.boost.ui.CfgEditorScreen
import ru.copilot.boost.ui.HomeScreen
import ru.copilot.boost.ui.LaunchArgsScreen
import ru.copilot.boost.ui.ModuleStubScreen
import ru.copilot.boost.ui.SetupSelectionScreen

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
                    onStartFlow = { includeTweaks, includeLaunchArgs, includeBinds ->
                        store.reset()
                        flowStore.startFlow(
                            includeTweaks = includeTweaks,
                            includeLaunchArgs = includeLaunchArgs,
                            includeBinds = includeBinds,
                        )
                    },
                    onBackHome = resetFlowToHome,
                )
            }

            AppScreen.ClientCfgUpload -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    BackNavigationBar(
                        title = "Загрузка клиентской конфигурации",
                        onBack = {
                            if (!flowStore.moveBackward()) {
                                resetFlowToSelection()
                            }
                        },
                        primaryActionText = if (flowStore.isCurrentFlowStep(AppScreen.ClientCfgUpload)) "Далее" else null,
                        primaryActionEnabled = store.state.hasFile,
                        onPrimaryAction = if (flowStore.isCurrentFlowStep(AppScreen.ClientCfgUpload)) {
                            {
                                if (!flowStore.moveForward()) {
                                    resetFlowToHome()
                                }
                            }
                        } else {
                            null
                        },
                    )
                    Box(modifier = Modifier.fillMaxSize()) {
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
            }

            AppScreen.Tweaks -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    BackNavigationBar(
                        title = "Твики",
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
                    )
                    Box(modifier = Modifier.fillMaxSize()) {
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
            }

            AppScreen.Binds -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    BackNavigationBar(
                        title = "Бинды",
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
                    )
                    Box(modifier = Modifier.fillMaxSize()) {
                        ModuleStubScreen(
                            title = "Функционал биндов",
                            description = "Здесь будет настройка биндов и пресетов клавиш.",
                        )
                    }
                }
            }

            AppScreen.LaunchArgs -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    BackNavigationBar(
                        title = "Параметры запуска",
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
                    )
                    Box(modifier = Modifier.fillMaxSize()) {
                        LaunchArgsScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun BackNavigationBar(
    title: String,
    onBack: () -> Unit,
    primaryActionText: String? = null,
    primaryActionEnabled: Boolean = true,
    onPrimaryAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            Button(onClick = onBack) {
                Text("Назад")
            }
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            if (primaryActionText != null && onPrimaryAction != null) {
                Button(
                    onClick = onPrimaryAction,
                    enabled = primaryActionEnabled,
                ) {
                    Text(primaryActionText)
                }
            }
        }
    }
}