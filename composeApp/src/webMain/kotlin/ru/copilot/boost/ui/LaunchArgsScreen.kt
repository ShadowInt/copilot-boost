package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.presentation.LaunchArgsStore

@Composable
fun LaunchArgsScreen(
    store: LaunchArgsStore,
) {
    val launchArgs = store.launchArgs

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
                .fillMaxSize(),
        ) {
            val columns = calculateColumns(maxWidth)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(),
            ) {
                LaunchArgsSettingsCard(
                    store = store,
                    modifier = Modifier.width(columns.left),
                )
                Spacer(modifier = Modifier.width(LaunchArgsUiSpec.InnerGap))
                LaunchArgsPreview(
                    launchArgs = launchArgs,
                    modifier = Modifier.width(columns.right),
                )
            }
        }
    }
}

@Composable
private fun LaunchArgsSettingsCard(
    store: LaunchArgsStore,
    modifier: Modifier = Modifier,
) {
    val state = store.state

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(Res.string.launch_args_title))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Gray)
                .padding(12.dp),
        ) {
            Column {
                SettingsSectionTitle(
                    stringResource(Res.string.launch_args_group_recommended),
                    withTopSpacing = false,
                    withBottomSpacing = true,
                )
                SettingsCheckboxRow(
                    label = stringResource(Res.string.launch_args_admin_teleport_label),
                    checked = state.adminTeleport,
                    onCheckedChange = store::onAdminTeleportChanged,
                    hint = stringResource(Res.string.launch_args_admin_teleport_hint),
                )

                SettingsSectionTitle(
                    stringResource(Res.string.launch_args_group_visual),
                    withBottomSpacing = true,
                )
                SettingsCheckboxRow(
                    label = stringResource(Res.string.launch_args_alt_head_turn_label),
                    checked = state.fasterAltHeadTurn,
                    onCheckedChange = store::onFasterAltHeadTurnChanged,
                    hint = stringResource(Res.string.launch_args_alt_head_turn_hint),
                )
                SettingsCheckboxRow(
                    label = stringResource(Res.string.launch_args_eyes_animation_label),
                    checked = state.disablePlayerEyesAnimation,
                    onCheckedChange = store::onDisablePlayerEyesAnimationChanged,
                    hint = stringResource(Res.string.launch_args_eyes_animation_hint),
                )

                SettingsSectionTitle(
                    stringResource(Res.string.launch_args_group_experimental),
                    withBottomSpacing = true,
                )
                SettingsCheckboxRow(
                    label = stringResource(Res.string.launch_args_server_hitmarker_label),
                    checked = state.serverHitmarker,
                    onCheckedChange = store::onServerHitmarkerChanged,
                    hint = stringResource(Res.string.launch_args_server_hitmarker_hint),
                )
                SettingsCheckboxRow(
                    label = stringResource(Res.string.launch_args_old_pickup_label),
                    checked = state.oldItemPickupNotifications,
                    onCheckedChange = store::onOldItemPickupNotificationsChanged,
                    hint = stringResource(Res.string.launch_args_old_pickup_hint),
                )
            }
        }
    }
}

@Composable
private fun LaunchArgsPreview(
    launchArgs: String,
    modifier: Modifier = Modifier,
) {
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
            if (launchArgs.isNotBlank()) {
                Text(
                    text = launchArgs,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                )
            } else {
                Text(
                    text = stringResource(Res.string.launch_args_preview_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
