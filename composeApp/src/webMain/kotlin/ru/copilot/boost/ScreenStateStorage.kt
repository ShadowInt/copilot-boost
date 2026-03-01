package ru.copilot.boost

import ru.copilot.boost.navigation.AppScreen

private const val SCREEN_STORAGE_KEY = "copilot.boost.currentScreen"

fun loadSavedScreen(): AppScreen {
    return AppScreen.Home
}

fun saveScreen(screen: AppScreen) {
    runCatching {
        writeLocalStorage(SCREEN_STORAGE_KEY, screen.name)
    }
}
