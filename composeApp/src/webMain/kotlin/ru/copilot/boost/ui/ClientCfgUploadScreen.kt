package ru.copilot.boost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import copilotboost.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.ui.components.FileDropZone

@Composable
fun ClientCfgUploadScreen(
    isDragging: Boolean,
    uploadError: String?,
    fileName: String?,
    onPickFileClick: () -> Unit,
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
        Text(stringResource(Res.string.upload_title), style = MaterialTheme.typography.headlineSmall)
        Text(
            text = stringResource(Res.string.upload_subtitle),
            modifier = Modifier.padding(top = 8.dp),
        )
        if (fileName != null) {
            Text(
                text = stringResource(Res.string.upload_selected_file, fileName),
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        FileDropZone(
            isDragging = isDragging,
            idleMessage = stringResource(Res.string.upload_idle_message),
            dragMessage = stringResource(Res.string.upload_drag_message),
            pickButtonText = stringResource(Res.string.upload_pick_button),
            onPickFileClick = onPickFileClick,
            errorMessage = uploadError,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}
