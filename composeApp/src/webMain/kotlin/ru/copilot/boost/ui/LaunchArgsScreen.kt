package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.Res
import copilotboost.composeapp.generated.resources.rust_steam_args_windows_en
import copilotboost.composeapp.generated.resources.rust_steam_args_windows_ru
import ru.copilot.boost.presentation.LaunchArgsStore
import ru.copilot.boost.copyTextToClipboard

@Composable
fun LaunchArgsScreen() {
    val store = remember { LaunchArgsStore() }
    val state = store.state
    val launchArgs = store.launchArgs
    val hasSelectedSettings = store.hasSelectedSettings
    val isCurrentSelectionCopied = store.isCurrentSelectionCopied
    val remainingStages = remember { defaultRemainingStages() }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize()
            .padding(LaunchArgsUiSpec.ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            val columns = calculateColumns(maxWidth)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(),
            ) {
                LaunchArgsSettingsCard(
                    adminTeleport = state.adminTeleport,
                    onAdminTeleportChanged = store::onAdminTeleportChanged,
                    fasterAltHeadTurn = state.fasterAltHeadTurn,
                    onFasterAltHeadTurnChanged = store::onFasterAltHeadTurnChanged,
                    disablePlayerEyesAnimation = state.disablePlayerEyesAnimation,
                    onDisablePlayerEyesAnimationChanged = store::onDisablePlayerEyesAnimationChanged,
                    serverHitmarker = state.serverHitmarker,
                    onServerHitmarkerChanged = store::onServerHitmarkerChanged,
                    oldItemPickupNotifications = state.oldItemPickupNotifications,
                    onOldItemPickupNotificationsChanged = store::onOldItemPickupNotificationsChanged,
                    modifier = Modifier.width(columns.left),
                )
                Spacer(modifier = Modifier.width(LaunchArgsUiSpec.InnerGap))
                LaunchArgsWindow(
                    launchArgs = launchArgs,
                    hasSelectedSettings = hasSelectedSettings,
                    isCurrentSelectionCopied = isCurrentSelectionCopied,
                    remainingStages = remainingStages,
                    onCopyClick = {
                        if (launchArgs.isNotBlank()) {
                            copyTextToClipboard(launchArgs)
                            store.onCopyConfirmed()
                        }
                    },
                    modifier = Modifier.width(columns.right),
                )
            }
        }
    }
}

private fun defaultRemainingStages(): List<LaunchStageDefinition> = listOf(
    LaunchStageDefinition(
        text = "Откройте свойства игры в Steam",
        imageResource = Res.drawable.rust_steam_args_windows_en,
    ),
    LaunchStageDefinition(
        text = "Вставьте параметры в поле запуска",
        imageResource = Res.drawable.rust_steam_args_windows_ru,
    ),
    LaunchStageDefinition(
        text = "Готово",
    ),
)

@Composable
private fun LaunchArgsSettingsCard(
    adminTeleport: Boolean,
    onAdminTeleportChanged: (Boolean) -> Unit,
    fasterAltHeadTurn: Boolean,
    onFasterAltHeadTurnChanged: (Boolean) -> Unit,
    disablePlayerEyesAnimation: Boolean,
    onDisablePlayerEyesAnimationChanged: (Boolean) -> Unit,
    serverHitmarker: Boolean,
    onServerHitmarkerChanged: (Boolean) -> Unit,
    oldItemPickupNotifications: Boolean,
    onOldItemPickupNotificationsChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val recommendedRows = listOf(
        LaunchSettingUiRow(
            label = "Админский телепорт",
            checked = adminTeleport,
            onCheckedChange = onAdminTeleportChanged,
            hint = "При наличии админки автоматически телепортирует игрока в точку установки маркера на карте.",
        ),
    )
    val visualRows = listOf(
        LaunchSettingUiRow(
            label = "Ускорить поворот головы через ALT",
            checked = fasterAltHeadTurn,
            onCheckedChange = onFasterAltHeadTurnChanged,
            hint = "При активации данного твика, голова персонажа будет быстрее возвращаться в исходное состояние при отпускании клавиши ALT.",
        ),
        LaunchSettingUiRow(
            label = "Отключить анимацию глаз игроков",
            checked = disablePlayerEyesAnimation,
            onCheckedChange = onDisablePlayerEyesAnimationChanged,
            hint = "Полностью отключает анимацию и моргания глаз у всех персонажей.",
        ),
    )
    val experimentalRows = listOf(
        LaunchSettingUiRow(
            label = "Серверный хитмаркер",
            checked = serverHitmarker,
            onCheckedChange = onServerHitmarkerChanged,
            hint = "При включении хитмаркер отображается только в том случае, когда сервер подтверждает регистрацию попадания. Добавляет небольшую задержку хитмаркерам, но избавляет от дезинформации.",
        ),
        LaunchSettingUiRow(
            label = "Старые уведомления о подборе предметов",
            checked = oldItemPickupNotifications,
            onCheckedChange = onOldItemPickupNotificationsChanged,
            hint = "При включении возвращает старый способ отображения подобранных предметов: каждый предмет отображается отдельно.",
        ),
    )

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
                .padding(12.dp),
        ) {
            Column {
                LaunchArgsSectionTitle("Рекомендуемые")
                recommendedRows.forEach { row ->
                    LaunchArgSettingRow(
                        label = row.label,
                        checked = row.checked,
                        onCheckedChange = row.onCheckedChange,
                        hint = row.hint,
                    )
                }

                LaunchArgsSectionTitle("Визуальные эффекты", withTopSpacing = true)
                visualRows.forEach { row ->
                    LaunchArgSettingRow(
                        label = row.label,
                        checked = row.checked,
                        onCheckedChange = row.onCheckedChange,
                        hint = row.hint,
                    )
                }

                LaunchArgsSectionTitle("Экспериментальные", withTopSpacing = true)
                experimentalRows.forEach { row ->
                    LaunchArgSettingRow(
                        label = row.label,
                        checked = row.checked,
                        onCheckedChange = row.onCheckedChange,
                        hint = row.hint,
                    )
                }
            }
        }
    }
}

private data class LaunchSettingUiRow(
    val label: String,
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit,
    val hint: String,
)

@Composable
private fun LaunchArgsSectionTitle(
    text: String,
    withTopSpacing: Boolean = false,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(
            top = if (withTopSpacing) 10.dp else 0.dp,
            start = 4.dp,
            bottom = 6.dp,
        ),
    )
}

@Composable
private fun LaunchArgSettingRow(
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
private fun LaunchArgsWindow(
    launchArgs: String,
    hasSelectedSettings: Boolean,
    isCurrentSelectionCopied: Boolean,
    remainingStages: List<LaunchStageDefinition>,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val totalStages = 1 + remainingStages.size
    val stageHeightsPx = remember(totalStages) {
        mutableStateListOf<Int>().apply { repeat(totalStages) { add(0) } }
    }
    val density = LocalDensity.current
    val stageSwitchThresholdPx = LaunchArgsUiSpec.STAGE_SWITCH_THRESHOLD_PX
    fun updateStageHeight(index: Int, newHeight: Int) {
        if (index in stageHeightsPx.indices && stageHeightsPx[index] != newHeight) {
            stageHeightsPx[index] = newHeight
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(LaunchArgsUiSpec.CardHeaderHeight)
                .padding(bottom = LaunchArgsUiSpec.CardHeaderBottomPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Настройка параметров запуска")
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Gray)
                .padding(12.dp),
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val viewportHeightPx = with(density) { maxHeight.roundToPx() }
                val progress = computeStageProgress(
                    stageHeightsPx = stageHeightsPx,
                    scrollValue = scrollState.value,
                    viewportHeightPx = viewportHeightPx,
                    maxScrollValue = scrollState.maxValue,
                    stageSwitchThresholdPx = stageSwitchThresholdPx,
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                ) {
                    LaunchArgsSelectStageWithCopy(
                        modifier = Modifier.onSizeChanged { updateStageHeight(index = 0, newHeight = it.height) },
                        launchArgs = launchArgs,
                        status = statusForStage(
                            index = 0,
                            hasSelectedSettings = hasSelectedSettings,
                            isCurrentSelectionCopied = isCurrentSelectionCopied,
                            progress = progress,
                        ),
                        isCopyEnabled = launchArgs.isNotBlank(),
                        onCopyClick = onCopyClick,
                    )
                    LaunchArgsStages(
                        stages = remainingStages,
                        stageStatusProvider = { stageIndex ->
                            statusForStage(
                                index = stageIndex + 1,
                                hasSelectedSettings = hasSelectedSettings,
                                isCurrentSelectionCopied = isCurrentSelectionCopied,
                                progress = progress,
                            )
                        },
                        stageModifiers = remainingStages.indices.associateWith { index ->
                            Modifier.onSizeChanged { updateStageHeight(index = index + 1, newHeight = it.height) }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LaunchArgsSelectStageWithCopy(
    modifier: Modifier = Modifier,
    launchArgs: String,
    status: LaunchStageStatus,
    isCopyEnabled: Boolean,
    onCopyClick: () -> Unit,
) {
    val markerColor = stageColor(status)
    val labelColor = stageTextColor(status)
    val isStarted = status != LaunchStageStatus.NOT_STARTED
    var contentHeightPx by remember { mutableIntStateOf(0) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {
        StageMarkerColumn(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxHeight(),
            color = markerColor,
            showConnector = true,
            contentHeightPx = contentHeightPx,
            markerTopOffset = 5.dp,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp)
                .onSizeChanged { contentHeightPx = it.height },
        ) {
            Text(
                text = "Выберите настройки и скопируйте их",
                style = MaterialTheme.typography.bodyMedium,
                color = labelColor,
                modifier = Modifier.padding(end = 4.dp),
            )
            if (isStarted) {
                Spacer(modifier = Modifier.height(8.dp))
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val copyButtonWidth = 72.dp
                    val fieldWidth = maxWidth - copyButtonWidth - LaunchArgsUiSpec.InnerGap
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(fieldWidth)
                                .heightIn(min = LaunchArgsUiSpec.CopyFieldMinHeight, max = LaunchArgsUiSpec.CopyFieldMaxHeight)
                                .border(1.dp, Color.Gray)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            Text(
                                text = launchArgs,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                            )
                        }
                        Spacer(modifier = Modifier.width(LaunchArgsUiSpec.InnerGap))
                        Button(
                            onClick = onCopyClick,
                            enabled = isCopyEnabled,
                            modifier = Modifier.width(copyButtonWidth),
                        ) {
                            CopyGlyph()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CopyGlyph() {
    Box(modifier = Modifier.size(14.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(10.dp)
                .border(1.dp, MaterialTheme.colorScheme.onPrimary),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(10.dp)
                .border(1.dp, MaterialTheme.colorScheme.onPrimary),
        )
    }
}
