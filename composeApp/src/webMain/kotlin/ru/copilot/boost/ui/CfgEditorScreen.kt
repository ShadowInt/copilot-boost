package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
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
    onDisableStrobeLightsChanged: (Boolean) -> Unit,
    onReduceHeldItemSizeChanged: (Boolean) -> Unit,
    onRestoreEventTextNotificationsChanged: (Boolean) -> Unit,
    onRemoveAutocraftMenuDelayChanged: (Boolean) -> Unit,
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
        verticalArrangement = Arrangement.Top,
    ) {
        if (!state.hasFile) {
            val dropZoneShape = RoundedCornerShape(16.dp)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(dropZoneShape)
                    .border(
                        width = 2.dp,
                        color = if (state.isDragging) MaterialTheme.colorScheme.primary else Color.Gray,
                        shape = dropZoneShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = if (state.isDragging) {
                            "Отпустите файл здесь"
                        } else {
                            "Перетащите client.cfg файл в эту область"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    if (!state.isDragging) {
                        Text(
                            text = "ИЛИ",
                            modifier = Modifier.padding(top = 10.dp),
                        )

                        Button(
                            onClick = onPickFileClick,
                            modifier = Modifier.padding(top = 16.dp),
                        ) {
                            Text("Выберите файл")
                        }
                    }

                    if (!state.isDragging) {
                        state.uploadError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 10.dp),
                            )
                        }
                    }
                }
            }

            return@Column
        }

        if (state.hasFile) {
            ThreeColumnEditorWithDownload(
                state = state,
                onDisableParasiticChanged = onDisableParasiticChanged,
                onDisableLegsRenderingChanged = onDisableLegsRenderingChanged,
                onDisableLegsDeformationChanged = onDisableLegsDeformationChanged,
                onDisableStrobeLightsChanged = onDisableStrobeLightsChanged,
                onReduceHeldItemSizeChanged = onReduceHeldItemSizeChanged,
                onRestoreEventTextNotificationsChanged = onRestoreEventTextNotificationsChanged,
                onRemoveAutocraftMenuDelayChanged = onRemoveAutocraftMenuDelayChanged,
                onReduceCameraShakeChanged = onReduceCameraShakeChanged,
                onImproveTreeMarkerVisibilityChanged = onImproveTreeMarkerVisibilityChanged,
                onDisableOcclusionCullingSafeModeChanged = onDisableOcclusionCullingSafeModeChanged,
                onDisableGibsCompletelyChanged = onDisableGibsCompletelyChanged,
                onDownloadClick = onDownloadClick,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
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
    onDisableStrobeLightsChanged: (Boolean) -> Unit,
    onReduceHeldItemSizeChanged: (Boolean) -> Unit,
    onRestoreEventTextNotificationsChanged: (Boolean) -> Unit,
    onRemoveAutocraftMenuDelayChanged: (Boolean) -> Unit,
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
        val baseLeftWidth = maxWidth * 0.33f
        val baseRemainingWidth = maxWidth - baseLeftWidth - gaps
        val fileColumnWidth = ((baseRemainingWidth - 8.dp) / 2) - 20.dp
        val leftWidth = maxWidth - (fileColumnWidth * 2) - gaps

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(),
            ) {
                SettingsColumn(
                    state = state,
                    onDisableParasiticChanged = onDisableParasiticChanged,
                    onDisableLegsRenderingChanged = onDisableLegsRenderingChanged,
                    onDisableLegsDeformationChanged = onDisableLegsDeformationChanged,
                    onDisableStrobeLightsChanged = onDisableStrobeLightsChanged,
                    onReduceHeldItemSizeChanged = onReduceHeldItemSizeChanged,
                    onRestoreEventTextNotificationsChanged = onRestoreEventTextNotificationsChanged,
                    onRemoveAutocraftMenuDelayChanged = onRemoveAutocraftMenuDelayChanged,
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
                    showDownloadButton = false,
                    onDownloadClick = null,
                    modifier = Modifier.width(fileColumnWidth),
                )
                Spacer(modifier = Modifier.width(8.dp))
                DiffColumn(
                    title = "Измененный",
                    diffRows = state.diffRows,
                    isNewColumn = true,
                    scrollState = sharedScrollState,
                    showDownloadButton = state.hasChanges,
                    onDownloadClick = onDownloadClick,
                    modifier = Modifier.width(fileColumnWidth),
                )
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
    onDisableStrobeLightsChanged: (Boolean) -> Unit,
    onReduceHeldItemSizeChanged: (Boolean) -> Unit,
    onRestoreEventTextNotificationsChanged: (Boolean) -> Unit,
    onRemoveAutocraftMenuDelayChanged: (Boolean) -> Unit,
    onReduceCameraShakeChanged: (Boolean) -> Unit,
    onImproveTreeMarkerVisibilityChanged: (Boolean) -> Unit,
    onDisableOcclusionCullingSafeModeChanged: (Boolean) -> Unit,
    onDisableGibsCompletelyChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Настройки")
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Gray)
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Column {
                SettingsGroupTitle("Рекомендуемые")
                SettingRow("Отключить все паразитные параметры", state.disableParasiticParameters, onDisableParasiticChanged)
                SettingRow("Отключить отображение ног", state.disableLegsRendering, onDisableLegsRenderingChanged)
                SettingRow("Уменьшить тряску камеры", state.reduceCameraShake, onReduceCameraShakeChanged)
                SettingRow("Улучшить видимость крестиков на деревьях", state.improveTreeMarkerVisibility, onImproveTreeMarkerVisibilityChanged)
                SettingRow("Отключить безопасный режим механизма отсечению окклюзии", state.disableOcclusionCullingSafeMode, onDisableOcclusionCullingSafeModeChanged)

                SettingsGroupTitle("Визуальные эффекты")
                SettingRow("Полностью отключить обломки", state.disableGibsCompletely, onDisableGibsCompletelyChanged)
                SettingRow("Отключить деформацию ног", state.disableLegsDeformation, onDisableLegsDeformationChanged)
                SettingRow("Отключить стробоскопы", state.disableStrobeLights, onDisableStrobeLightsChanged)
                SettingRow("Уменьшить предмет в руках", state.reduceHeldItemSize, onReduceHeldItemSizeChanged)

                SettingsGroupTitle("Интерфейс")
                SettingRow(
                    "Вернуть текстовые уведомления об ивентах",
                    state.restoreEventTextNotifications,
                    onRestoreEventTextNotificationsChanged,
                )
                SettingRow(
                    "Убрать задержку в меню автокрафта",
                    state.removeAutocraftMenuDelay,
                    onRemoveAutocraftMenuDelayChanged,
                )

            }
        }
    }
}

@Composable
private fun SettingsGroupTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(top = 10.dp, start = 4.dp),
    )
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
    showDownloadButton: Boolean,
    onDownloadClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = title)
            Box(
                modifier = Modifier.width(132.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (showDownloadButton && onDownloadClick != null) {
                    Button(onClick = onDownloadClick) {
                        Text("Скачать")
                    }
                }
            }
        }
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
                            if (isNewColumn) Color(0x10AA0000) else Color(0x1AAA0000)
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
