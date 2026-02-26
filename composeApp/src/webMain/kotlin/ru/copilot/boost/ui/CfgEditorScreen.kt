package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import ru.copilot.boost.domain.model.DiffRow
import ru.copilot.boost.domain.model.DiffRowType
import ru.copilot.boost.presentation.model.CfgEditorUiState

@Composable
fun CfgEditorScreen(
    state: CfgEditorUiState,
    onPickFileClick: () -> Unit,
    onDisableParasiticChanged: (Boolean) -> Unit,
    onDisableLegsRenderingChanged: (Boolean) -> Unit,
    onDisableLegsDeformationChanged: (Boolean) -> Unit,
    onReduceCameraShakeChanged: (Boolean) -> Unit,
    onImproveTreeMarkerVisibilityChanged: (Boolean) -> Unit,
    onDisableOcclusionCullingSafeModeChanged: (Boolean) -> Unit,
    onDisableGibsCompletelyChanged: (Boolean) -> Unit,
    onDownloadClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Загрузка файла",
            style = MaterialTheme.typography.headlineSmall,
        )

        if (!state.hasFile) {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(170.dp)
                    .border(
                        width = 2.dp,
                        color = if (state.isDragging) MaterialTheme.colorScheme.primary else Color.Gray,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (state.isDragging) {
                        "Отпустите файл здесь"
                    } else {
                        "Перетащите файл в эту область"
                    },
                )
            }

            Button(
                onClick = onPickFileClick,
                modifier = Modifier.padding(top = 16.dp),
            ) {
                Text("Выбрать файл")
            }
        }

        state.uploadError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 10.dp),
            )
        }

        state.fileName?.let { fileName ->
            Text(
                text = "Файл: $fileName",
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        if (state.hasFile) {
            ThreeColumnEditorWithDownload(
                state = state,
                onDisableParasiticChanged = onDisableParasiticChanged,
                onDisableLegsRenderingChanged = onDisableLegsRenderingChanged,
                onDisableLegsDeformationChanged = onDisableLegsDeformationChanged,
                onReduceCameraShakeChanged = onReduceCameraShakeChanged,
                onImproveTreeMarkerVisibilityChanged = onImproveTreeMarkerVisibilityChanged,
                onDisableOcclusionCullingSafeModeChanged = onDisableOcclusionCullingSafeModeChanged,
                onDisableGibsCompletelyChanged = onDisableGibsCompletelyChanged,
                onDownloadClick = onDownloadClick,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .height(410.dp),
            )
        }
    }
}

@Composable
private fun ThreeColumnEditorWithDownload(
    state: CfgEditorUiState,
    onDisableParasiticChanged: (Boolean) -> Unit,
    onDisableLegsRenderingChanged: (Boolean) -> Unit,
    onDisableLegsDeformationChanged: (Boolean) -> Unit,
    onReduceCameraShakeChanged: (Boolean) -> Unit,
    onImproveTreeMarkerVisibilityChanged: (Boolean) -> Unit,
    onDisableOcclusionCullingSafeModeChanged: (Boolean) -> Unit,
    onDisableGibsCompletelyChanged: (Boolean) -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sharedScrollState = rememberScrollState()
    BoxWithConstraints(modifier = modifier) {
        val gaps = 16.dp
        val leftWidth = maxWidth * 0.33f
        val remainingWidth = maxWidth - leftWidth - gaps
        val fileColumnWidth = (remainingWidth - 8.dp) / 2

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
            ) {
                SettingsColumn(
                    state = state,
                    onDisableParasiticChanged = onDisableParasiticChanged,
                    onDisableLegsRenderingChanged = onDisableLegsRenderingChanged,
                    onDisableLegsDeformationChanged = onDisableLegsDeformationChanged,
                    onReduceCameraShakeChanged = onReduceCameraShakeChanged,
                    onImproveTreeMarkerVisibilityChanged = onImproveTreeMarkerVisibilityChanged,
                    onDisableOcclusionCullingSafeModeChanged = onDisableOcclusionCullingSafeModeChanged,
                    onDisableGibsCompletelyChanged = onDisableGibsCompletelyChanged,
                    modifier = Modifier.width(leftWidth),
                )
                Spacer(modifier = Modifier.width(8.dp))
                DiffColumn(
                    title = "Исходный",
                    diffRows = state.diffRows,
                    isNewColumn = false,
                    scrollState = sharedScrollState,
                    modifier = Modifier.width(fileColumnWidth),
                )
                Spacer(modifier = Modifier.width(8.dp))
                DiffColumn(
                    title = "Измененный",
                    diffRows = state.diffRows,
                    isNewColumn = true,
                    scrollState = sharedScrollState,
                    modifier = Modifier.width(fileColumnWidth),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.width(leftWidth + 8.dp + fileColumnWidth + 8.dp))
                Box(
                    modifier = Modifier.width(fileColumnWidth),
                    contentAlignment = Alignment.Center,
                ) {
                    if (state.hasChanges) {
                        Button(onClick = onDownloadClick) {
                            Text("Скачать измененный файл")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsColumn(
    state: CfgEditorUiState,
    onDisableParasiticChanged: (Boolean) -> Unit,
    onDisableLegsRenderingChanged: (Boolean) -> Unit,
    onDisableLegsDeformationChanged: (Boolean) -> Unit,
    onReduceCameraShakeChanged: (Boolean) -> Unit,
    onImproveTreeMarkerVisibilityChanged: (Boolean) -> Unit,
    onDisableOcclusionCullingSafeModeChanged: (Boolean) -> Unit,
    onDisableGibsCompletelyChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .border(1.dp, Color.Gray)
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Column {
            Text("Настройки", style = MaterialTheme.typography.titleSmall)
            SettingRow("Отключить все паразитные параметры", state.disableParasiticParameters, onDisableParasiticChanged)
            SettingRow("Отключить отображение ног", state.disableLegsRendering, onDisableLegsRenderingChanged)
            SettingRow("Отключить деформацию ног", state.disableLegsDeformation, onDisableLegsDeformationChanged)
            SettingRow("Уменьшить тряску камеры", state.reduceCameraShake, onReduceCameraShakeChanged)
            SettingRow("Улучшить видимость крестиков на деревьях", state.improveTreeMarkerVisibility, onImproveTreeMarkerVisibilityChanged)
            SettingRow("Отключить безопасный режим механизма отсечения окклюзий", state.disableOcclusionCullingSafeMode, onDisableOcclusionCullingSafeModeChanged)
            SettingRow("Полностью отключить обломки", state.disableGibsCompletely, onDisableGibsCompletelyChanged)
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(top = 6.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Text(label)
    }
}

@Composable
private fun DiffColumn(
    title: String,
    diffRows: List<DiffRow>,
    isNewColumn: Boolean,
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Gray)
                .padding(12.dp)
                .verticalScroll(scrollState),
        ) {
            Column {
                diffRows.forEach { row ->
                    val text = if (isNewColumn) row.newLine.orEmpty() else row.oldLine.orEmpty()
                    val bg = when (row.type) {
                        DiffRowType.MODIFIED -> {
                            if (isNewColumn) Color(0x1A00AA00) else Color(0x1AAA0000)
                        }
                        DiffRowType.ADDED -> {
                            if (isNewColumn) Color(0x1A00AA00) else Color.Transparent
                        }
                        DiffRowType.REMOVED -> {
                            if (isNewColumn) Color.Transparent else Color(0x1AAA0000)
                        }
                        DiffRowType.UNCHANGED -> Color.Transparent
                    }
                    val displayLine = if (text.isEmpty()) " " else text

                    Text(
                        text = displayLine,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bg),
                        fontFamily = FontFamily.Monospace,
                    )
                }
                if (diffRows.isEmpty()) {
                    Text(
                        text = "Нет данных",
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        }
    }
}
