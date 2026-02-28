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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.Res
import copilotboost.composeapp.generated.resources.rust_steam_args_windows_en
import copilotboost.composeapp.generated.resources.rust_steam_args_windows_ru
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.launch
import ru.copilot.boost.copyTextToClipboard

@Composable
fun LaunchArgsScreen() {
    var adminTeleport by rememberSaveable { mutableStateOf(false) }
    var fasterAltHeadTurn by rememberSaveable { mutableStateOf(false) }
    var disablePlayerEyesAnimation by rememberSaveable { mutableStateOf(false) }
    var serverHitmarker by rememberSaveable { mutableStateOf(false) }
    var oldItemPickupNotifications by rememberSaveable { mutableStateOf(false) }
    var lastCopiedLaunchArgs by rememberSaveable { mutableStateOf<String?>(null) }
    var steamPropertiesStageCompleted by rememberSaveable { mutableStateOf(false) }
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
    val firstStageStatus = when {
        !hasSelectedSettings -> LaunchStageStatus.NOT_STARTED
        isCurrentSelectionCopied -> LaunchStageStatus.COMPLETED
        else -> LaunchStageStatus.IN_PROGRESS
    }
    val canContinueSteamStage = firstStageStatus == LaunchStageStatus.COMPLETED
    LaunchedEffect(canContinueSteamStage) {
        if (!canContinueSteamStage) steamPropertiesStageCompleted = false
    }
    val steamStageStatus = when {
        !canContinueSteamStage -> LaunchStageStatus.NOT_STARTED
        steamPropertiesStageCompleted -> LaunchStageStatus.COMPLETED
        else -> LaunchStageStatus.IN_PROGRESS
    }
    val remainingStages = listOf(
        LaunchStageItem(
            text = "Откройте свойства игры в Steam",
            status = steamStageStatus,
            showNextButton = true,
            isNextButtonEnabled = steamStageStatus == LaunchStageStatus.IN_PROGRESS,
            onNextClick = { steamPropertiesStageCompleted = true },
            imageResource = Res.drawable.rust_steam_args_windows_en,
        ),
        LaunchStageItem(
            text = "Вставьте параметры в поле запуска",
            status = if (steamPropertiesStageCompleted) {
                LaunchStageStatus.IN_PROGRESS
            } else {
                LaunchStageStatus.NOT_STARTED
            },
            imageResource = Res.drawable.rust_steam_args_windows_ru,
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
                    firstStageStatus = firstStageStatus,
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
    firstStageStatus: LaunchStageStatus,
    remainingStages: List<LaunchStageItem>,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val steamStageRequester = remember { BringIntoViewRequester() }
    val finalStageRequester = remember { BringIntoViewRequester() }
    val stagesForRender = remember(remainingStages) {
        remainingStages.mapIndexed { index, stage ->
            if (index == 0 && stage.onNextClick != null) {
                stage.copy(
                    onNextClick = {
                        stage.onNextClick.invoke()
                        scope.launch { finalStageRequester.bringIntoView() }
                    },
                )
            } else {
                stage
            }
        }
    }

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
                    .verticalScroll(rememberScrollState()),
            ) {
                LaunchArgsSelectStageWithCopy(
                    launchArgs = launchArgs,
                    status = firstStageStatus,
                    isCopyEnabled = launchArgs.isNotBlank(),
                    onCopyClick = {
                        onCopyClick()
                        scope.launch { steamStageRequester.bringIntoView() }
                    },
                )
                LaunchArgsStages(
                    stages = stagesForRender,
                    stageRequesters = mapOf(
                        0 to steamStageRequester,
                        1 to finalStageRequester,
                    ),
                )
            }
        }
    }
}

@Composable
private fun LaunchArgsStages(
    stages: List<LaunchStageItem>,
    stageRequesters: Map<Int, BringIntoViewRequester> = emptyMap(),
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {
        stages.forEachIndexed { index, stage ->
            LaunchArgsStageRow(
                modifier = stageRequesters[index]
                    ?.let { Modifier.bringIntoViewRequester(it) }
                    ?: Modifier,
                text = stage.text,
                status = stage.status,
                showConnector = index != stages.lastIndex,
                showNextButton = stage.showNextButton,
                isNextButtonEnabled = stage.isNextButtonEnabled,
                onNextClick = stage.onNextClick,
                imageResource = stage.imageResource,
            )
        }
    }
}

@Composable
private fun LaunchArgsSelectStageWithCopy(
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
        modifier = Modifier
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
private fun LaunchArgsStageRow(
    modifier: Modifier = Modifier,
    text: String,
    status: LaunchStageStatus,
    showConnector: Boolean,
    showNextButton: Boolean = false,
    isNextButtonEnabled: Boolean = true,
    onNextClick: (() -> Unit)? = null,
    imageResource: DrawableResource? = null,
) {
    val markerColor = stageColor(status)
    val labelColor = stageTextColor(status)
    val isStarted = status != LaunchStageStatus.NOT_STARTED
    var contentHeightPx by remember { mutableIntStateOf(0) }
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        StageMarkerColumn(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxHeight(),
            color = markerColor,
            showConnector = showConnector,
            contentHeightPx = contentHeightPx,
            markerTopOffset = 5.dp,
        )
        Column(
            modifier = Modifier
                .padding(start = 24.dp)
                .onSizeChanged { contentHeightPx = it.height },
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = labelColor,
                modifier = Modifier.padding(top = 0.dp, end = 4.dp),
            )
            if (isStarted) {
                if (imageResource != null && showNextButton && onNextClick != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val buttonWidth = 88.dp
                        val imageWidth = (maxWidth - buttonWidth - 8.dp) * 0.78f
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(imageResource),
                                contentDescription = text,
                                modifier = Modifier
                                    .width(imageWidth)
                                    .aspectRatio(844f / 600f)
                                    .border(1.dp, Color(0x33000000)),
                                contentScale = ContentScale.Fit,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onNextClick,
                                enabled = isNextButtonEnabled,
                                modifier = Modifier.width(buttonWidth),
                            ) {
                                Text(
                                    text = "Далее",
                                    maxLines = 1,
                                    softWrap = false,
                                )
                            }
                        }
                    }
                } else {
                    if (imageResource != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(imageResource),
                            contentDescription = text,
                            modifier = Modifier
                                .fillMaxWidth(0.82f)
                                .aspectRatio(844f / 600f)
                                .border(1.dp, Color(0x33000000)),
                            contentScale = ContentScale.Fit,
                        )
                    }
                    if (showNextButton && onNextClick != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = onNextClick,
                            enabled = isNextButtonEnabled,
                        ) {
                            Text(
                                text = "Далее",
                                maxLines = 1,
                                softWrap = false,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StageMarkerColumn(
    modifier: Modifier = Modifier,
    color: Color,
    showConnector: Boolean,
    contentHeightPx: Int,
    markerTopOffset: androidx.compose.ui.unit.Dp = 0.dp,
) {
    val density = LocalDensity.current
    val markerColumnHeight = with(density) {
        if (contentHeightPx > 0) contentHeightPx.toDp() else 10.dp
    }
    Box(
        modifier = modifier
            .width(16.dp)
            .height(markerColumnHeight)
            .drawBehind {
                if (showConnector) {
                    val markerSize = 10.dp.toPx()
                    val gapBelowMarker = 2.dp.toPx()
                    val lineWidth = 2.dp.toPx()
                    val startY = markerTopOffset.toPx() + markerSize + gapBelowMarker
                    val lineHeight = (size.height - startY).coerceAtLeast(0f)
                    drawRect(
                        color = color.copy(alpha = 0.55f),
                        topLeft = Offset((size.width - lineWidth) / 2f, startY),
                        size = Size(lineWidth, lineHeight),
                    )
                }
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .padding(top = markerTopOffset)
                .size(10.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(50),
                ),
        )
    }
}

private enum class LaunchStageStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
}

private data class LaunchStageItem(
    val text: String,
    val status: LaunchStageStatus,
    val showNextButton: Boolean = false,
    val isNextButtonEnabled: Boolean = true,
    val onNextClick: (() -> Unit)? = null,
    val imageResource: DrawableResource? = null,
)

@Composable
private fun stageColor(status: LaunchStageStatus): Color = when (status) {
    LaunchStageStatus.NOT_STARTED -> MaterialTheme.colorScheme.outlineVariant
    LaunchStageStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
    LaunchStageStatus.COMPLETED -> Color(0xFF2E7D32)
}

@Composable
private fun stageTextColor(status: LaunchStageStatus): Color = when (status) {
    LaunchStageStatus.NOT_STARTED -> MaterialTheme.colorScheme.onSurfaceVariant
    LaunchStageStatus.IN_PROGRESS -> MaterialTheme.colorScheme.onSurface
    LaunchStageStatus.COMPLETED -> MaterialTheme.colorScheme.onSurface
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
