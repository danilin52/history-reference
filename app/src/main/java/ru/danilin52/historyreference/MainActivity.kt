package ru.danilin52.historyreference

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.danilin52.historyreference.ui.nav.AppNav
import ru.danilin52.historyreference.ui.theme.HistoryReferenceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as HistoryApp
        setContent {
            val darkTheme by app.preferences.darkTheme.collectAsStateWithLifecycle()
            HistoryReferenceTheme(darkTheme = darkTheme) {
                AppNav()
            }
        }
    }
}
