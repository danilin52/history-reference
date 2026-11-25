package ru.danilin52.historyreference.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.ContextCompat
import ru.danilin52.historyreference.notifications.OnThisDayScheduler
import ru.danilin52.historyreference.preferences.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(preferences: UserPreferences) {
    val context = LocalContext.current
    val userName by preferences.userName.collectAsStateWithLifecycle()
    val darkTheme by preferences.darkTheme.collectAsStateWithLifecycle()
    val notificationsEnabled by preferences.notificationsEnabled.collectAsStateWithLifecycle()
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        preferences.setNotificationsEnabled(granted)
        if (granted) {
            OnThisDayScheduler.schedule(context)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Настройки") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = userName,
                onValueChange = preferences::setUserName,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Имя пользователя") },
                singleLine = true
            )

            SettingSwitch(
                title = "Тёмная тема",
                checked = darkTheme,
                onCheckedChange = preferences::setDarkTheme
            )

            SettingSwitch(
                title = "Уведомления «В этот день»",
                checked = notificationsEnabled,
                onCheckedChange = { enabled ->
                    if (!enabled) {
                        preferences.setNotificationsEnabled(false)
                        OnThisDayScheduler.cancel(context)
                    } else if (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    } else {
                        preferences.setNotificationsEnabled(true)
                        OnThisDayScheduler.schedule(context)
                    }
                }
            )

            Text(
                text = "Настройки сохраняются в SharedPreferences и остаются после перезапуска приложения.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
