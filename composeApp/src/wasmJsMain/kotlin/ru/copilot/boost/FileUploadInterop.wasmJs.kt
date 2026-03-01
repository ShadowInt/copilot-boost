package ru.copilot.boost

import kotlinx.browser.window
import org.w3c.dom.DragEvent
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlin.js.ExperimentalWasmJsInterop
import ru.copilot.boost.model.UploadedFileData

actual fun observeGlobalFileDrop(
    onDragStateChanged: (Boolean) -> Unit,
    onFileSelected: (UploadedFileData) -> Unit,
    onInvalidFile: () -> Unit,
    allowedFileName: String,
): () -> Unit {
    val dragOverListener: (Event) -> Unit = { event ->
        val dragEvent = event as DragEvent
        dragEvent.preventDefault()
        onDragStateChanged(true)
    }

    val dragLeaveListener: (Event) -> Unit = { event ->
        event.preventDefault()
        onDragStateChanged(false)
    }

    val dropListener: (Event) -> Unit = { event ->
        val dragEvent = event as DragEvent
        dragEvent.preventDefault()
        onDragStateChanged(false)
        val file = dragEvent.dataTransfer?.files?.item(0)
        if (file != null) {
            if (isAllowedCfgFile(file.name, allowedFileName)) {
                readFileAsText(file) { content ->
                    onFileSelected(UploadedFileData(name = file.name, content = content))
                }
            } else {
                onInvalidFile()
            }
        }
    }

    window.document.addEventListener("dragover", dragOverListener)
    window.document.addEventListener("dragleave", dragLeaveListener)
    window.document.addEventListener("drop", dropListener)

    return {
        window.document.removeEventListener("dragover", dragOverListener)
        window.document.removeEventListener("dragleave", dragLeaveListener)
        window.document.removeEventListener("drop", dropListener)
    }
}

actual fun openFilePicker(
    onFileSelected: (UploadedFileData) -> Unit,
    onInvalidFile: () -> Unit,
    allowedFileName: String,
) {
    val input = window.document.createElement("input") as HTMLInputElement
    input.type = "file"
    input.accept = ".cfg"
    input.style.display = "none"

    input.onchange = {
        val file = input.files?.item(0)
        if (file != null) {
            if (isAllowedCfgFile(file.name, allowedFileName)) {
                readFileAsText(file) { content ->
                    onFileSelected(UploadedFileData(name = file.name, content = content))
                }
            } else {
                onInvalidFile()
            }
        }
        input.remove()
        null
    }

    window.document.body?.appendChild(input)
    input.click()
}

actual fun downloadCfgFile(fileName: String, content: String) {
    val base64Content = window.btoa(content)
    val anchor = window.document.createElement("a") as HTMLAnchorElement
    anchor.href = "data:text/plain;base64,$base64Content"
    anchor.download = fileName
    window.document.body?.appendChild(anchor)
    anchor.click()
    anchor.remove()
}

actual fun copyTextToClipboard(text: String) {
    writeTextToClipboard(text)
}

actual fun readLocalStorage(key: String): String? = window.localStorage.getItem(key)

actual fun writeLocalStorage(key: String, value: String) {
    window.localStorage.setItem(key, value)
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun readFileAsText(file: File, onRead: (String) -> Unit) {
    val reader = FileReader()
    reader.onload = {
        onRead(reader.result?.toString().orEmpty())
        null
    }
    reader.readAsText(file)
}

private fun isAllowedCfgFile(fileName: String, allowedFileName: String): Boolean = fileName == allowedFileName

@OptIn(ExperimentalWasmJsInterop::class)
private fun writeTextToClipboard(text: String) {
    js("window.navigator && window.navigator.clipboard && window.navigator.clipboard.writeText(text)")
}
