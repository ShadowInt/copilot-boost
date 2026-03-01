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
import ru.copilot.boost.ui.components.FileDropZone

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
    onReduceSleepingBagRemovalDelayChanged: (Boolean) -> Unit,
    onAddMapInfoToF8MenuChanged: (Boolean) -> Unit,
    onDisableClientErrorOverlayChanged: (Boolean) -> Unit,
    onAddAdminGesturesToGameMenuChanged: (Boolean) -> Unit,
    onConvenientSkinSortingChanged: (Boolean) -> Unit,
    onEnlargedConsoleChanged: (Boolean) -> Unit,
    onReduceRadialMenuCallDelayChanged: (Boolean) -> Unit,
    onLeftHandModeChanged: (Boolean) -> Unit,
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
            FileDropZone(
                isDragging = state.isDragging,
                idleMessage = "Перетащите client.cfg файл в эту область",
                dragMessage = "Отпустите файл здесь",
                pickButtonText = "Выберите файл",
                onPickFileClick = onPickFileClick,
                errorMessage = state.uploadError,
            )
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
                onReduceSleepingBagRemovalDelayChanged = onReduceSleepingBagRemovalDelayChanged,
                onAddMapInfoToF8MenuChanged = onAddMapInfoToF8MenuChanged,
                onDisableClientErrorOverlayChanged = onDisableClientErrorOverlayChanged,
                onAddAdminGesturesToGameMenuChanged = onAddAdminGesturesToGameMenuChanged,
                onConvenientSkinSortingChanged = onConvenientSkinSortingChanged,
                onEnlargedConsoleChanged = onEnlargedConsoleChanged,
                onReduceRadialMenuCallDelayChanged = onReduceRadialMenuCallDelayChanged,
                onLeftHandModeChanged = onLeftHandModeChanged,
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
    onReduceSleepingBagRemovalDelayChanged: (Boolean) -> Unit,
    onAddMapInfoToF8MenuChanged: (Boolean) -> Unit,
    onDisableClientErrorOverlayChanged: (Boolean) -> Unit,
    onAddAdminGesturesToGameMenuChanged: (Boolean) -> Unit,
    onConvenientSkinSortingChanged: (Boolean) -> Unit,
    onEnlargedConsoleChanged: (Boolean) -> Unit,
    onReduceRadialMenuCallDelayChanged: (Boolean) -> Unit,
    onLeftHandModeChanged: (Boolean) -> Unit,
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
                SettingsCard(
                    state = state,
                    onDisableParasiticChanged = onDisableParasiticChanged,
                    onDisableLegsRenderingChanged = onDisableLegsRenderingChanged,
                    onDisableLegsDeformationChanged = onDisableLegsDeformationChanged,
                    onDisableStrobeLightsChanged = onDisableStrobeLightsChanged,
                    onReduceHeldItemSizeChanged = onReduceHeldItemSizeChanged,
                    onRestoreEventTextNotificationsChanged = onRestoreEventTextNotificationsChanged,
                    onRemoveAutocraftMenuDelayChanged = onRemoveAutocraftMenuDelayChanged,
                    onReduceSleepingBagRemovalDelayChanged = onReduceSleepingBagRemovalDelayChanged,
                    onAddMapInfoToF8MenuChanged = onAddMapInfoToF8MenuChanged,
                    onDisableClientErrorOverlayChanged = onDisableClientErrorOverlayChanged,
                    onAddAdminGesturesToGameMenuChanged = onAddAdminGesturesToGameMenuChanged,
                    onConvenientSkinSortingChanged = onConvenientSkinSortingChanged,
                    onEnlargedConsoleChanged = onEnlargedConsoleChanged,
                    onReduceRadialMenuCallDelayChanged = onReduceRadialMenuCallDelayChanged,
                    onLeftHandModeChanged = onLeftHandModeChanged,
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
internal fun SettingsCard(
    state: CfgEditorUiState,
    onDisableParasiticChanged: (Boolean) -> Unit,
    onDisableLegsRenderingChanged: (Boolean) -> Unit,
    onDisableLegsDeformationChanged: (Boolean) -> Unit,
    onDisableStrobeLightsChanged: (Boolean) -> Unit,
    onReduceHeldItemSizeChanged: (Boolean) -> Unit,
    onRestoreEventTextNotificationsChanged: (Boolean) -> Unit,
    onRemoveAutocraftMenuDelayChanged: (Boolean) -> Unit,
    onReduceSleepingBagRemovalDelayChanged: (Boolean) -> Unit,
    onAddMapInfoToF8MenuChanged: (Boolean) -> Unit,
    onDisableClientErrorOverlayChanged: (Boolean) -> Unit,
    onAddAdminGesturesToGameMenuChanged: (Boolean) -> Unit,
    onConvenientSkinSortingChanged: (Boolean) -> Unit,
    onEnlargedConsoleChanged: (Boolean) -> Unit,
    onReduceRadialMenuCallDelayChanged: (Boolean) -> Unit,
    onLeftHandModeChanged: (Boolean) -> Unit,
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
                SettingRow(
                    label = "Отключить все паразитные параметры",
                    checked = state.disableParasiticParameters,
                    onCheckedChange = onDisableParasiticChanged,
                    hint = "Отключает все ненужные настройки, которые затрудняют геймплей, снижают производительность или просто удалены из игры, но остались в меню (ненужные блики, размытия, уведомления, анимации и т.д.)",
                )
                SettingRow(
                    label = "Отключить отображение ног",
                    checked = state.disableLegsRendering,
                    onCheckedChange = onDisableLegsRenderingChanged,
                    hint = "Отключает отображение ног персонажа от первого лица, помогая целиться вниз.",
                )
                SettingRow(
                    label = "Уменьшить тряску камеры",
                    checked = state.reduceCameraShake,
                    onCheckedChange = onReduceCameraShakeChanged,
                    hint = "Минимизирует тряску камеры при беге, стрельбе, взрывах и получении урона.",
                )
                SettingRow(
                    label = "Улучшить видимость крестиков на деревьях",
                    checked = state.improveTreeMarkerVisibility,
                    onCheckedChange = onImproveTreeMarkerVisibilityChanged,
                    hint = "Меняет цвет отображения маркеров на деревьях на более контрастный.",
                )
                SettingRow(
                    label = "Отключить безопасный режим механизма отсечению окклюзии",
                    checked = state.disableOcclusionCullingSafeMode,
                    onCheckedChange = onDisableOcclusionCullingSafeModeChanged,
                    hint = "Проверка, что безопасный режим отсечения окклюзии, который очень сильно снижает FPS, отключен.",
                )

                SettingsGroupTitle("Визуальные эффекты")
                SettingRow(
                    label = "Полностью отключить обломки",
                    checked = state.disableGibsCompletely,
                    onCheckedChange = onDisableGibsCompletelyChanged,
                    hint = "Полностью отключает обломки при разрушении сооружений и объектов. Звуки разрушений при этом сохраняются.",
                )
                SettingRow(
                    label = "Отключить деформацию ног",
                    checked = state.disableLegsDeformation,
                    onCheckedChange = onDisableLegsDeformationChanged,
                    hint = "Возвращает старую анимацию ног персонажа, которая не подстраивается под рельеф. Может помочь в PVP и немного снизить нагрузку на ПК.",
                )
                SettingRow(
                    label = "Отключить стробоскопы",
                    checked = state.disableStrobeLights,
                    onCheckedChange = onDisableStrobeLightsChanged,
                    hint = "Полностью отключает стробоскопы в игре, оставляя только звук их работы. Может сильно повысить FPS, если рядом несколько стробоскопов.",
                )
                SettingRow(
                    label = "Уменьшить предмет в руках",
                    checked = state.reduceHeldItemSize,
                    onCheckedChange = onReduceHeldItemSizeChanged,
                    hint = "Делает оружие и предметы в руках персонажа меньше. Функция работает только если уголь обзора установлен выше 70.",
                )

                SettingsGroupTitle("Интерфейс")
                SettingRow(
                    label = "Вернуть текстовые уведомления об ивентах",
                    checked = state.restoreEventTextNotifications,
                    onCheckedChange = onRestoreEventTextNotificationsChanged,
                    hint = "Возвращает текстовые уведомления о старте ивентов, таких как появление на карте карго, патрульного вертолета, чинука и т.п.",
                )
                SettingRow(
                    label = "Убрать задержку в меню автокрафта",
                    checked = state.removeAutocraftMenuDelay,
                    onCheckedChange = onRemoveAutocraftMenuDelayChanged,
                    hint = "Отключает небольшое провисание интерфейса при попытке скрафтить предмет через меню быстрого крафта.",
                )
                SettingRow(
                    label = "Снизить задержку при удалении спальников",
                    checked = state.reduceSleepingBagRemovalDelay,
                    onCheckedChange = onReduceSleepingBagRemovalDelayChanged,
                    hint = "Твик позволяет практически мгновенно удалять спальники, кликнув на крестик на карте.",
                )
                SettingRow(
                    label = "Добавить информацию о карте в меню F8",
                    checked = state.addMapInfoToF8Menu,
                    onCheckedChange = onAddMapInfoToF8MenuChanged,
                    hint = "Добавляет информацию о типе, размере и сиде карты в меню отладки F8. У администраторов серверов также отображаются коорлинаты персонажа.",
                )
                SettingRow(
                    label = "Отключить отображение клиентских ошибок",
                    checked = state.disableClientErrorOverlay,
                    onCheckedChange = onDisableClientErrorOverlayChanged,
                    hint = "Полностью отключает надоедливые красные ошибки в углу экрана.",
                )
                SettingRow(
                    label = "Добавить админские жесты в игровое меню",
                    checked = state.addAdminGesturesToGameMenu,
                    onCheckedChange = onAddAdminGesturesToGameMenuChanged,
                    hint = "Добавляет скрытые анимации в меню настройки жестов. Эти анимации работают только при наличии админки на сервере.",
                )
                SettingRow(
                    label = "Удобная сортировка скинов",
                    checked = state.convenientSkinSorting,
                    onCheckedChange = onConvenientSkinSortingChanged,
                    hint = "Сортирует скины в меню крафта по дате использования.",
                )
                SettingRow(
                    label = "Увеличенная консоль",
                    checked = state.enlargedConsole,
                    onCheckedChange = onEnlargedConsoleChanged,
                    hint = "Увеличивает размер шрифта в консоли.",
                )

                SettingsGroupTitle("Экспериментальные")
                SettingRow(
                    label = "Снизить задержку вызова радиального меню",
                    checked = state.reduceRadialMenuCallDelay,
                    onCheckedChange = onReduceRadialMenuCallDelayChanged,
                    hint = "Немного ускоряет появление радиального меню при зажатии кнопки взаимодействия. Может быть не привычно. Не рекомендуется игрокам с низким FPS.",
                )
                SettingRow(
                    label = "Режим левой руки",
                    checked = state.leftHandMode,
                    onCheckedChange = onLeftHandModeChanged,
                    hint = "Переносит оружие и предметы в левую руку персонажа",
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
    hint: String? = null,
) {
    SettingsCheckboxRow(
        label = label,
        checked = checked,
        onCheckedChange = onCheckedChange,
        hint = hint,
    )
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
