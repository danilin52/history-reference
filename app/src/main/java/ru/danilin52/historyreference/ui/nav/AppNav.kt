package ru.danilin52.historyreference.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import ru.danilin52.historyreference.HistoryApp
import ru.danilin52.historyreference.ui.main.MainScreen

@Composable
fun AppNav() {
    val context = LocalContext.current
    val app = context.applicationContext as HistoryApp
    val repository = app.repository

    LaunchedEffect(Unit) {
        repository.ensureSeeded()
    }

    MainScreen(
        repository = repository,
        preferences = app.preferences
    )
}
