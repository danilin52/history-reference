package ru.danilin52.historyreference

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.danilin52.historyreference.ui.nav.AppNav
import ru.danilin52.historyreference.ui.theme.HistoryReferenceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HistoryReferenceTheme {
                AppNav()
            }
        }
    }
}