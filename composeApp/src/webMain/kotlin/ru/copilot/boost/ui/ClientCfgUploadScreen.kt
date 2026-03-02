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
        Text("Загрузка client.cfg", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Перетащите файл или выберите его вручную",
            modifier = Modifier.padding(top = 8.dp),
        )
        if (fileName != null) {
            Text(
                text = "Выбран файл: $fileName",
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        FileDropZone(
            isDragging = isDragging,
            idleMessage = "Перетащите client.cfg файл в эту область",
            dragMessage = "Отпустите файл здесь",
            pickButtonText = "Выберите файл",
            onPickFileClick = onPickFileClick,
            errorMessage = uploadError,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}
