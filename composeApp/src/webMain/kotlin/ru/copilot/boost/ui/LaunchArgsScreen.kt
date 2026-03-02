package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.presentation.LaunchArgsStore
import ru.copilot.boost.copyTextToClipboard
import ru.copilot.boost.ui.components.stage.StageDefinition
import ru.copilot.boost.ui.components.stage.StageMarkerColumn
import ru.copilot.boost.ui.components.stage.StageStatus
import ru.copilot.boost.ui.components.stage.StageTimeline
import ru.copilot.boost.ui.components.stage.stageStatusColor
import ru.copilot.boost.ui.components.stage.stageStatusTextColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LaunchArgsScreen(
    store: LaunchArgsStore,
) {
    val state = store.state
    val launchArgs = store.launchArgs
    val hasSelectedSettings = store.hasSelectedSettings
    val isCurrentSelectionCopied = store.isCurrentSelectionCopied
    val remainingStages = defaultRemainingStages()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val copiedMessage = stringResource(Res.string.launch_args_copied)

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(LaunchArgsUiSpec.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(),
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
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    launch {
                                        snackbarHostState.showSnackbar(
                                            message = copiedMessage,
                                            duration = SnackbarDuration.Indefinite,
                                        )
                                    }
                                    delay(1200)
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                }
                            }
                        },
                        modifier = Modifier.width(columns.right),
                    )
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .widthIn(max = 200.dp)
                .padding(16.dp),
        )
    }
}

@Composable
private fun defaultRemainingStages(): List<StageDefinition> = listOf(
    StageDefinition(
        text = stringResource(Res.string.launch_args_stage_open_steam),
        imageResource = Res.drawable.lib_steam_macos_ru,
    ),
    StageDefinition(
        text = stringResource(Res.string.launch_args_stage_paste_params),
        imageResource = Res.drawable.rust_steam_args_macos_ru,
    ),
    StageDefinition(
        text = stringResource(Res.string.launch_args_stage_done),
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
            label = stringResource(Res.string.launch_args_admin_teleport_label),
            checked = adminTeleport,
            onCheckedChange = onAdminTeleportChanged,
            hint = stringResource(Res.string.launch_args_admin_teleport_hint),
        ),
    )
    val visualRows = listOf(
        LaunchSettingUiRow(
            label = stringResource(Res.string.launch_args_alt_head_turn_label),
            checked = fasterAltHeadTurn,
            onCheckedChange = onFasterAltHeadTurnChanged,
            hint = stringResource(Res.string.launch_args_alt_head_turn_hint),
        ),
        LaunchSettingUiRow(
            label = stringResource(Res.string.launch_args_eyes_animation_label),
            checked = disablePlayerEyesAnimation,
            onCheckedChange = onDisablePlayerEyesAnimationChanged,
            hint = stringResource(Res.string.launch_args_eyes_animation_hint),
        ),
    )
    val experimentalRows = listOf(
        LaunchSettingUiRow(
            label = stringResource(Res.string.launch_args_server_hitmarker_label),
            checked = serverHitmarker,
            onCheckedChange = onServerHitmarkerChanged,
            hint = stringResource(Res.string.launch_args_server_hitmarker_hint),
        ),
        LaunchSettingUiRow(
            label = stringResource(Res.string.launch_args_old_pickup_label),
            checked = oldItemPickupNotifications,
            onCheckedChange = onOldItemPickupNotificationsChanged,
            hint = stringResource(Res.string.launch_args_old_pickup_hint),
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
            Text(text = stringResource(Res.string.launch_args_settings_title))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Gray)
                .padding(12.dp),
        ) {
            Column {
                LaunchArgsSectionTitle(stringResource(Res.string.launch_args_group_recommended))
                recommendedRows.forEach { row ->
                    LaunchArgSettingRow(
                        label = row.label,
                        checked = row.checked,
                        onCheckedChange = row.onCheckedChange,
                        hint = row.hint,
                    )
                }

                LaunchArgsSectionTitle(stringResource(Res.string.launch_args_group_visual), withTopSpacing = true)
                visualRows.forEach { row ->
                    LaunchArgSettingRow(
                        label = row.label,
                        checked = row.checked,
                        onCheckedChange = row.onCheckedChange,
                        hint = row.hint,
                    )
                }

                LaunchArgsSectionTitle(stringResource(Res.string.launch_args_group_experimental), withTopSpacing = true)
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
    remainingStages: List<StageDefinition>,
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
            Text(text = stringResource(Res.string.launch_args_window_title))
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
                    StageTimeline(
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
    status: StageStatus,
    isCopyEnabled: Boolean,
    onCopyClick: () -> Unit,
) {
    val markerColor = stageStatusColor(status)
    val labelColor = stageStatusTextColor(status)
    val isStarted = status != StageStatus.NOT_STARTED
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
                text = stringResource(Res.string.launch_args_select_and_copy),
                style = MaterialTheme.typography.bodyMedium,
                color = labelColor,
                modifier = Modifier.padding(end = 4.dp),
            )
            if (isStarted) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = LaunchArgsUiSpec.CopyFieldMinHeight, max = LaunchArgsUiSpec.CopyFieldMaxHeight)
                        .border(1.dp, Color.Gray)
                        .padding(start = 12.dp, top = 6.dp, end = 4.dp, bottom = 6.dp),
                ) {
                    Text(
                        text = launchArgs,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 36.dp)
                            .verticalScroll(rememberScrollState())
                            .align(Alignment.CenterStart),
                    )
                    IconButton(
                        onClick = onCopyClick,
                        enabled = isCopyEnabled,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterEnd),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = stringResource(Res.string.launch_args_copy_button),
                        )
                    }
                }
            }
        }
    }
}
