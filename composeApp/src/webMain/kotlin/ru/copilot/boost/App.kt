package ru.copilot.boost

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import ru.copilot.boost.presentation.CfgEditorStore
import ru.copilot.boost.ui.CfgEditorScreen

@Composable
fun App() {
    val store = remember { CfgEditorStore() }

    DisposableEffect(store) {
        val disposeListeners = observeGlobalFileDrop(
            onDragStateChanged = store::onDragStateChanged,
            onFileSelected = store::onFileSelected,
            onInvalidFile = store::onInvalidFile,
        )

        onDispose { disposeListeners() }
    }

    MaterialTheme {
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