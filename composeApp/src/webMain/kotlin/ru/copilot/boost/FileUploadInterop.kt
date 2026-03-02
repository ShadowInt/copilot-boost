package ru.copilot.boost

import ru.copilot.boost.model.UploadedFileData

expect fun observeGlobalFileDrop(
    onDragStateChanged: (Boolean) -> Unit,
    onFileSelected: (UploadedFileData) -> Unit,
    onInvalidFile: () -> Unit,
): () -> Unit

expect fun openFilePicker(
    onFileSelected: (UploadedFileData) -> Unit,
    onInvalidFile: () -> Unit,
)

expect fun downloadCfgFile(fileName: String, content: String)

expect fun copyTextToClipboard(text: String)

expect fun readLocalStorage(key: String): String?

expect fun writeLocalStorage(key: String, value: String)

expect fun observePageUnloadWarning(message: String): () -> Unit
