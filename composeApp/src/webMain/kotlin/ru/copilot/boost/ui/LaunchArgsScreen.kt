package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.Res
import copilotboost.composeapp.generated.resources.rust_steam_args_windows_en
import copilotboost.composeapp.generated.resources.rust_steam_args_windows_ru
import ru.copilot.boost.copyTextToClipboard

@Composable
fun LaunchArgsScreen() {
    var adminTeleport by rememberSaveable { mutableStateOf(false) }
    var fasterAltHeadTurn by rememberSaveable { mutableStateOf(false) }
    var disablePlayerEyesAnimation by rememberSaveable { mutableStateOf(false) }
    var serverHitmarker by rememberSaveable { mutableStateOf(false) }
    var oldItemPickupNotifications by rememberSaveable { mutableStateOf(false) }
    var lastCopiedLaunchArgs by rememberSaveable { mutableStateOf<String?>(null) }
    val launchArgs = buildList {
        if (adminTeleport) add("-global.enable_marker_teleport \"True\"")
        if (fasterAltHeadTurn) {
            add("-client.headlerp \"10\"")
            add("-headlerp_inertia \"0\"")
        }
        if (disablePlayerEyesAnimation) {
            add("-player.eye_blinking \"False\"")
            add("-player.eye_movement \"False\"")
        }
        if (serverHitmarker) add("-hitnotify.notification_level \"2\"")
        if (oldItemPickupNotifications) {
            add("-global.showitempickupnotices \"1\"")
            add("-global.showitemcountsonpickup \"False\"")
            add("-global.usesingleitempickupnotice \"False\"")
        }
    }.joinToString(" ")
    val hasSelectedSettings = launchArgs.isNotBlank()
    val isCurrentSelectionCopied = hasSelectedSettings && lastCopiedLaunchArgs == launchArgs
    val remainingStages = listOf(
        LaunchStageItem(
            text = "Откройте свойства игры в Steam",
            status = LaunchStageStatus.NOT_STARTED,
            imageResource = Res.drawable.rust_steam_args_windows_en,
        ),
        LaunchStageItem(
            text = "Вставьте параметры в поле запуска",
            status = LaunchStageStatus.NOT_STARTED,
            imageResource = Res.drawable.rust_steam_args_windows_ru,
        ),
        LaunchStageItem(
            text = "Готово",
            status = LaunchStageStatus.NOT_STARTED,
        ),
    )

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            val gaps = 16.dp
            val baseLeftWidth = maxWidth * 0.33f
            val baseRemainingWidth = maxWidth - baseLeftWidth - gaps
            val rightWidth = ((baseRemainingWidth - 8.dp) / 2) * 2 - 40.dp
            val leftWidth = maxWidth - rightWidth - gaps

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(),
            ) {
                LaunchArgsSettingsCard(
                    adminTeleport = adminTeleport,
                    onAdminTeleportChanged = { adminTeleport = it },
                    fasterAltHeadTurn = fasterAltHeadTurn,
                    onFasterAltHeadTurnChanged = { fasterAltHeadTurn = it },
                    disablePlayerEyesAnimation = disablePlayerEyesAnimation,
                    onDisablePlayerEyesAnimationChanged = { disablePlayerEyesAnimation = it },
                    serverHitmarker = serverHitmarker,
                    onServerHitmarkerChanged = { serverHitmarker = it },
                    oldItemPickupNotifications = oldItemPickupNotifications,
                    onOldItemPickupNotificationsChanged = { oldItemPickupNotifications = it },
                    modifier = Modifier.width(leftWidth),
                )
                Spacer(modifier = Modifier.width(8.dp))
                LaunchArgsWindow(
                    launchArgs = launchArgs,
                    hasSelectedSettings = hasSelectedSettings,
                    isCurrentSelectionCopied = isCurrentSelectionCopied,
                    remainingStages = remainingStages,
                    onCopyClick = {
                        if (launchArgs.isNotBlank()) {
                            copyTextToClipboard(launchArgs)
                            lastCopiedLaunchArgs = launchArgs
                        }
                    },
                    modifier = Modifier.width(rightWidth),
                )
            }
        }
    }
}

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
                LaunchArgSettingRow(
                    label = "Админский телепорт",
                    checked = adminTeleport,
                    onCheckedChange = onAdminTeleportChanged,
                    hint = "При наличии админки автоматически телепортирует игрока в точку установки маркера на карте.",
                )

                LaunchArgsSectionTitle("Визуальные эффекты", withTopSpacing = true)
                LaunchArgSettingRow(
                    label = "Ускорить поворот головы через ALT",
                    checked = fasterAltHeadTurn,
                    onCheckedChange = onFasterAltHeadTurnChanged,
                    hint = "При активации данного твика, голова персонажа будет быстрее возвращаться в исходное состояние при отпускании клавиши ALT.",
                )
                LaunchArgSettingRow(
                    label = "Отключить анимацию глаз игроков",
                    checked = disablePlayerEyesAnimation,
                    onCheckedChange = onDisablePlayerEyesAnimationChanged,
                    hint = "Полностью отключает анимацию и моргания глаз у всех персонажей.",
                )

                LaunchArgsSectionTitle("Экспериментальные", withTopSpacing = true)
                LaunchArgSettingRow(
                    label = "Серверный хитмаркер",
                    checked = serverHitmarker,
                    onCheckedChange = onServerHitmarkerChanged,
                    hint = "При включении хитмаркер отображается только в том случае, когда сервер подтверждает регистрацию попадания. Добавляет небольшую задержку хитмаркерам, но избавляет от дезинформации.",
                )
                LaunchArgSettingRow(
                    label = "Старые уведомления о подборе предметов",
                    checked = oldItemPickupNotifications,
                    onCheckedChange = onOldItemPickupNotificationsChanged,
                    hint = "При включении возвращает старый способ отображения подобранных предметов: каждый предмет отображается отдельно.",
                )
            }
        }
    }
}

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
    remainingStages: List<LaunchStageItem>,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    var firstStageHeightPx by remember { mutableIntStateOf(0) }
    var secondStageHeightPx by remember { mutableIntStateOf(0) }
    var thirdStageHeightPx by remember { mutableIntStateOf(0) }
    var viewportHeightPx by remember { mutableIntStateOf(0) }
    val firstStageEnd = firstStageHeightPx
    val secondStageEnd = firstStageHeightPx + secondStageHeightPx
    val thirdStageEnd = secondStageEnd + thirdStageHeightPx
    val viewportBottom = scrollState.value + viewportHeightPx
    val stageSwitchThresholdPx = 24
    val activeStageIndex = when {
        viewportBottom >= thirdStageEnd - stageSwitchThresholdPx -> 3
        viewportBottom >= secondStageEnd - stageSwitchThresholdPx -> 2
        viewportBottom >= firstStageEnd - stageSwitchThresholdPx -> 1
        else -> 0
    }
    val effectiveActiveStageIndex = if (isCurrentSelectionCopied) {
        activeStageIndex.coerceAtLeast(1)
    } else {
        activeStageIndex
    }
    val isAtBottom = scrollState.value >= (scrollState.maxValue - stageSwitchThresholdPx).coerceAtLeast(0)
    val lastStageIndex = 3
    fun statusFor(index: Int): LaunchStageStatus = when {
        !hasSelectedSettings -> LaunchStageStatus.NOT_STARTED
        !isCurrentSelectionCopied && index == 0 -> LaunchStageStatus.IN_PROGRESS
        !isCurrentSelectionCopied -> LaunchStageStatus.NOT_STARTED
        isAtBottom && index == lastStageIndex -> LaunchStageStatus.COMPLETED
        index < effectiveActiveStageIndex -> LaunchStageStatus.COMPLETED
        index == effectiveActiveStageIndex -> LaunchStageStatus.IN_PROGRESS
        else -> LaunchStageStatus.NOT_STARTED
    }
    val stagesForRender = listOf(
        remainingStages[0].copy(status = statusFor(1)),
        remainingStages[1].copy(status = statusFor(2)),
        remainingStages[2].copy(status = statusFor(3)),
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(bottom = 6.dp),
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { viewportHeightPx = it.height }
                    .verticalScroll(scrollState),
            ) {
                LaunchArgsSelectStageWithCopy(
                    modifier = Modifier.onSizeChanged { firstStageHeightPx = it.height },
                    launchArgs = launchArgs,
                    status = statusFor(0),
                    isCopyEnabled = launchArgs.isNotBlank(),
                    onCopyClick = onCopyClick,
                )
                LaunchArgsStages(
                    stages = stagesForRender,
                    stageModifiers = mapOf(
                        0 to Modifier.onSizeChanged { secondStageHeightPx = it.height },
                        1 to Modifier.onSizeChanged { thirdStageHeightPx = it.height },
                    ),
                )
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
                    val fieldWidth = maxWidth - copyButtonWidth - 8.dp
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(fieldWidth)
                                .heightIn(min = 48.dp, max = 180.dp)
                                .border(1.dp, Color.Gray)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            Text(
                                text = launchArgs,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
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
