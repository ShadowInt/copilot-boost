package ru.copilot.boost

import ru.copilot.boost.model.UploadedFileData

expect fun observeGlobalFileDrop(
    onDragStateChanged: (Boolean) -> Unit,
    onFileSelected: (UploadedFileData) -> Unit,
    onInvalidFile: () -> Unit,
    allowedFileName: String = "client.cfg",
): () -> Unit

expect fun openFilePicker(
    onFileSelected: (UploadedFileData) -> Unit,
    onInvalidFile: () -> Unit,
    allowedFileName: String = "client.cfg",
)

expect fun downloadCfgFile(fileName: String, content: String)

expect fun copyTextToClipboard(text: String)

expect fun readLocalStorage(key: String): String?

expect fun writeLocalStorage(key: String, value: String)
