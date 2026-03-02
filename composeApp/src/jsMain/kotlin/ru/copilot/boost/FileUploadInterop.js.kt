package ru.copilot.boost

import org.w3c.dom.events.Event
import org.w3c.files.File
import org.w3c.files.FileReader

internal actual fun platformReadFileAsText(file: File, onRead: (String) -> Unit) {
    val reader = FileReader()
    reader.onload = {
        onRead((reader.result as? String).orEmpty())
        null
    }
    reader.readAsText(file)
}

internal actual fun platformSetBeforeUnloadReturnValue(event: Event, message: String) {
    event.asDynamic().returnValue = message
}

internal actual fun platformWriteTextToClipboard(text: String) {
    js("window.navigator && window.navigator.clipboard && window.navigator.clipboard.writeText(text)")
}
