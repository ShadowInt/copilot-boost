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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun LaunchArgsScreen() {
    var adminTeleport by rememberSaveable { mutableStateOf(false) }
    var fasterAltHeadTurn by rememberSaveable { mutableStateOf(false) }
    var disablePlayerEyesAnimation by rememberSaveable { mutableStateOf(false) }
    var serverHitmarker by rememberSaveable { mutableStateOf(false) }
    var oldItemPickupNotifications by rememberSaveable { mutableStateOf(false) }
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

    val clipboard = LocalClipboard.current
    var copyRequestText by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(copyRequestText) {
        val textToCopy = copyRequestText ?: return@LaunchedEffect
        clipboard.setClipEntry(ClipEntry.withPlainText(textToCopy))
        copyRequestText = null
    }

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
                    onCopyClick = { copyRequestText = launchArgs },
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
    Row(
        modifier = Modifier
            .padding(top = 6.dp)
            .fillMaxWidth(),
        verticalAlignment = if (hint == null) Alignment.CenterVertically else Alignment.Top,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(label)
            if (hint != null) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, end = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun LaunchArgsWindow(
    launchArgs: String,
    onCopyClick: () -> Unit,
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
            Text(text = "Параметры запуска")
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Gray)
                .padding(12.dp),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SteamWindowTopBar()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ПАРАМЕТРЫ ЗАПУСКА",
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(10.dp))
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val copyButtonWidth = 72.dp
                    val fieldWidth = maxWidth - copyButtonWidth - 8.dp
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = launchArgs,
                            onValueChange = {},
                            readOnly = true,
                            singleLine = false,
                            modifier = Modifier
                                .width(fieldWidth)
                                .height(120.dp),
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onCopyClick,
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
private fun SteamWindowTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WindowControlDot(Color(0xFFE35B5B))
        Spacer(modifier = Modifier.width(6.dp))
        WindowControlDot(Color(0xFFE3C35B))
        Spacer(modifier = Modifier.width(6.dp))
        WindowControlDot(Color(0xFF6BCB77))
    }
}

@Composable
private fun WindowControlDot(color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .background(color, RoundedCornerShape(50)),
    )
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
