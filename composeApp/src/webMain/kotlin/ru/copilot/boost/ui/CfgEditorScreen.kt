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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.domain.model.DiffRow
import ru.copilot.boost.domain.model.DiffRowType
import ru.copilot.boost.domain.model.PresetId
import ru.copilot.boost.presentation.model.CfgEditorUiState

@Composable
fun CfgEditorScreen(
    state: CfgEditorUiState,
    onPresetChanged: (PresetId, Boolean) -> Unit,
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
            Text(
                text = stringResource(Res.string.cfg_no_file),
                modifier = Modifier.padding(top = 12.dp),
            )
            return@Column
        }

        ThreeColumnEditorWithDownload(
            state = state,
            onPresetChanged = onPresetChanged,
            onDownloadClick = onDownloadClick,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
        )
    }
}

@Composable
private fun ThreeColumnEditorWithDownload(
    state: CfgEditorUiState,
    onPresetChanged: (PresetId, Boolean) -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sharedScrollState = rememberScrollState()
    var showOnlyChanges by remember { mutableStateOf(true) }
    val diffRowsToShow = if (showOnlyChanges) {
        state.diffRows.filter { it.type != DiffRowType.UNCHANGED }
    } else {
        state.diffRows
    }

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
                    onPresetChanged = onPresetChanged,
                    modifier = Modifier.width(leftWidth),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.width((fileColumnWidth * 2) + 8.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = showOnlyChanges,
                            onCheckedChange = { showOnlyChanges = it },
                        )
                        Text(stringResource(Res.string.cfg_only_changes))
                    }
                    Row(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        DiffColumn(
                            title = stringResource(Res.string.cfg_column_original),
                            diffRows = diffRowsToShow,
                            isNewColumn = false,
                            scrollState = sharedScrollState,
                            showDownloadButton = false,
                            onDownloadClick = null,
                            modifier = Modifier.width(fileColumnWidth),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DiffColumn(
                            title = stringResource(Res.string.cfg_column_modified),
                            diffRows = diffRowsToShow,
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
    }
}

@Composable
internal fun SettingsCard(
    state: CfgEditorUiState,
    onPresetChanged: (PresetId, Boolean) -> Unit,
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
            Text(text = stringResource(Res.string.cfg_settings_title))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Gray)
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Column {
                SettingsGroupTitle(stringResource(Res.string.cfg_group_recommended))
                SettingRow(
                    label = stringResource(Res.string.preset_parasitic_label),
                    checked = state.presets[PresetId.DisableParasiticParameters],
                    onCheckedChange = { onPresetChanged(PresetId.DisableParasiticParameters, it) },
                    hint = stringResource(Res.string.preset_parasitic_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_legs_rendering_label),
                    checked = state.presets[PresetId.DisableLegsRendering],
                    onCheckedChange = { onPresetChanged(PresetId.DisableLegsRendering, it) },
                    hint = stringResource(Res.string.preset_legs_rendering_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_camera_shake_label),
                    checked = state.presets[PresetId.ReduceCameraShake],
                    onCheckedChange = { onPresetChanged(PresetId.ReduceCameraShake, it) },
                    hint = stringResource(Res.string.preset_camera_shake_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_tree_marker_label),
                    checked = state.presets[PresetId.ImproveTreeMarkerVisibility],
                    onCheckedChange = { onPresetChanged(PresetId.ImproveTreeMarkerVisibility, it) },
                    hint = stringResource(Res.string.preset_tree_marker_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_occlusion_culling_label),
                    checked = state.presets[PresetId.DisableOcclusionCullingSafeMode],
                    onCheckedChange = { onPresetChanged(PresetId.DisableOcclusionCullingSafeMode, it) },
                    hint = stringResource(Res.string.preset_occlusion_culling_hint),
                )

                SettingsGroupTitle(stringResource(Res.string.cfg_group_visual))
                SettingRow(
                    label = stringResource(Res.string.preset_gibs_label),
                    checked = state.presets[PresetId.DisableGibsCompletely],
                    onCheckedChange = { onPresetChanged(PresetId.DisableGibsCompletely, it) },
                    hint = stringResource(Res.string.preset_gibs_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_legs_deformation_label),
                    checked = state.presets[PresetId.DisableLegsDeformation],
                    onCheckedChange = { onPresetChanged(PresetId.DisableLegsDeformation, it) },
                    hint = stringResource(Res.string.preset_legs_deformation_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_strobe_lights_label),
                    checked = state.presets[PresetId.DisableStrobeLights],
                    onCheckedChange = { onPresetChanged(PresetId.DisableStrobeLights, it) },
                    hint = stringResource(Res.string.preset_strobe_lights_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_held_item_size_label),
                    checked = state.presets[PresetId.ReduceHeldItemSize],
                    onCheckedChange = { onPresetChanged(PresetId.ReduceHeldItemSize, it) },
                    hint = stringResource(Res.string.preset_held_item_size_hint),
                )

                SettingsGroupTitle(stringResource(Res.string.cfg_group_interface))
                SettingRow(
                    label = stringResource(Res.string.preset_event_notifications_label),
                    checked = state.presets[PresetId.RestoreEventTextNotifications],
                    onCheckedChange = { onPresetChanged(PresetId.RestoreEventTextNotifications, it) },
                    hint = stringResource(Res.string.preset_event_notifications_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_autocraft_delay_label),
                    checked = state.presets[PresetId.RemoveAutocraftMenuDelay],
                    onCheckedChange = { onPresetChanged(PresetId.RemoveAutocraftMenuDelay, it) },
                    hint = stringResource(Res.string.preset_autocraft_delay_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_sleeping_bag_label),
                    checked = state.presets[PresetId.ReduceSleepingBagRemovalDelay],
                    onCheckedChange = { onPresetChanged(PresetId.ReduceSleepingBagRemovalDelay, it) },
                    hint = stringResource(Res.string.preset_sleeping_bag_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_f8_map_info_label),
                    checked = state.presets[PresetId.AddMapInfoToF8Menu],
                    onCheckedChange = { onPresetChanged(PresetId.AddMapInfoToF8Menu, it) },
                    hint = stringResource(Res.string.preset_f8_map_info_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_error_overlay_label),
                    checked = state.presets[PresetId.DisableClientErrorOverlay],
                    onCheckedChange = { onPresetChanged(PresetId.DisableClientErrorOverlay, it) },
                    hint = stringResource(Res.string.preset_error_overlay_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_admin_gestures_label),
                    checked = state.presets[PresetId.AddAdminGesturesToGameMenu],
                    onCheckedChange = { onPresetChanged(PresetId.AddAdminGesturesToGameMenu, it) },
                    hint = stringResource(Res.string.preset_admin_gestures_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_skin_sorting_label),
                    checked = state.presets[PresetId.ConvenientSkinSorting],
                    onCheckedChange = { onPresetChanged(PresetId.ConvenientSkinSorting, it) },
                    hint = stringResource(Res.string.preset_skin_sorting_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_enlarged_console_label),
                    checked = state.presets[PresetId.EnlargedConsole],
                    onCheckedChange = { onPresetChanged(PresetId.EnlargedConsole, it) },
                    hint = stringResource(Res.string.preset_enlarged_console_hint),
                )

                SettingsGroupTitle(stringResource(Res.string.cfg_group_experimental))
                SettingRow(
                    label = stringResource(Res.string.preset_radial_menu_label),
                    checked = state.presets[PresetId.ReduceRadialMenuCallDelay],
                    onCheckedChange = { onPresetChanged(PresetId.ReduceRadialMenuCallDelay, it) },
                    hint = stringResource(Res.string.preset_radial_menu_hint),
                )
                SettingRow(
                    label = stringResource(Res.string.preset_left_hand_label),
                    checked = state.presets[PresetId.LeftHandMode],
                    onCheckedChange = { onPresetChanged(PresetId.LeftHandMode, it) },
                    hint = stringResource(Res.string.preset_left_hand_hint),
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
                        Text(stringResource(Res.string.cfg_download))
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
                diffRows.forEachIndexed { index, row ->
                    key(index) {
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
                }
                if (diffRows.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.cfg_no_data),
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        }
    }
}
