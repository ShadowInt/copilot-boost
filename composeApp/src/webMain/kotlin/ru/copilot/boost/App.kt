package ru.copilot.boost

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.copilot.boost.navigation.AppScreen
import ru.copilot.boost.presentation.CfgEditorStore
import ru.copilot.boost.ui.CfgEditorScreen
import ru.copilot.boost.ui.HomeScreen
import ru.copilot.boost.ui.ModuleStubScreen
import ru.copilot.boost.ui.TopNavigation

@Composable
fun App() {
    val store = remember { CfgEditorStore() }
    var currentScreen by remember { mutableStateOf(loadSavedScreen()) }

    LaunchedEffect(currentScreen) {
        saveScreen(currentScreen)
    }

    if (currentScreen == AppScreen.Tweaks) {
        DisposableEffect(store) {
            val disposeListeners = observeGlobalFileDrop(
                onDragStateChanged = store::onDragStateChanged,
                onFileSelected = store::onFileSelected,
                onInvalidFile = store::onInvalidFile,
            )

            onDispose { disposeListeners() }
        }
    }

    MaterialTheme {
        when (currentScreen) {
            AppScreen.Home -> {
                HomeScreen(
                    appVersion = BuildKonfig.PROJECT_VERSION,
                    onOpenTweaks = { currentScreen = AppScreen.Tweaks },
                    onOpenBinds = { currentScreen = AppScreen.Binds },
                    onOpenLaunchArgs = { currentScreen = AppScreen.LaunchArgs },
                )
            }

            AppScreen.Tweaks -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopNavigation(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                    )
                    Box(modifier = Modifier.fillMaxSize()) {
                        val state = store.state
                        CfgEditorScreen(
                            state = state,
                            onPickFileClick = {
                                openFilePicker(
                                    onFileSelected = store::onFileSelected,
                                    onInvalidFile = store::onInvalidFile,
                                )
                            },
                            onDisableParasiticChanged = store::onDisableParasiticChanged,
                            onDisableLegsRenderingChanged = store::onDisableLegsRenderingChanged,
                            onDisableLegsDeformationChanged = store::onDisableLegsDeformationChanged,
                            onDisableStrobeLightsChanged = store::onDisableStrobeLightsChanged,
                            onReduceHeldItemSizeChanged = store::onReduceHeldItemSizeChanged,
                            onRestoreEventTextNotificationsChanged = store::onRestoreEventTextNotificationsChanged,
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
                ModuleStubScreen(
                    title = "Функционал биндов",
                    description = "Здесь будет настройка биндов и пресетов клавиш.",
                    onBackHome = { currentScreen = AppScreen.Home },
                )
            }

            AppScreen.LaunchArgs -> {
                ModuleStubScreen(
                    title = "Параметры запуска",
                    description = "Здесь будет настройка и генерация параметров запуска.",
                    onBackHome = { currentScreen = AppScreen.Home },
                )
            }
        }
    }
}