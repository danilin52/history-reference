package ru.danilin52.historyreference.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.CircleShape
import kotlinx.coroutines.launch
import ru.danilin52.historyreference.data.HistoryRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    repository: HistoryRepository,
    personId: Long,
    onBack: () -> Unit,
    onEventClick: (Long) -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit
) {
    val person by repository.person(personId).collectAsStateWithLifecycle(initialValue = null)
    val events by repository.eventsForPerson(personId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(person?.name ?: "Личность") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit, enabled = person != null) {
                        Icon(Icons.Filled.Edit, contentDescription = "Изменить личность")
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = person != null
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Удалить личность")
                    }
                }
            )
        }
    ) { padding ->
        val current = person ?: return@Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            current.portraitUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = current.name,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(12.dp))
            }

            Text(current.occupation, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Text(
                "${current.birthYear ?: "?"} — ${current.deathYear ?: "?"}",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(12.dp))
            Text(current.biography, style = MaterialTheme.typography.bodyMedium)

            if (events.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text("Связанные события", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    events.forEach { event ->
                        Text(
                            "${event.date} · ${event.title}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { onEventClick(event.id) }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить личность?") },
            text = { Text("Личность и все её связи с событиями будут удалены.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val current = person ?: return@TextButton
                        showDeleteDialog = false
                        scope.launch {
                            repository.deletePerson(current)
                            onDeleted()
                        }
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}
