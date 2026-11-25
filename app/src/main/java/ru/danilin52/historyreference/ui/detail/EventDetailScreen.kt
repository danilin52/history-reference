package ru.danilin52.historyreference.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ru.danilin52.historyreference.data.HistoryRepository
import ru.danilin52.historyreference.ui.components.categoryLabel
import ru.danilin52.historyreference.ui.components.roleLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    repository: HistoryRepository,
    eventId: Long,
    onBack: () -> Unit,
    onPersonClick: (Long) -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit,
    onManagePeople: () -> Unit
) {
    val event by repository.event(eventId).collectAsStateWithLifecycle(initialValue = null)
    val links by repository.eventPersonItems(eventId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event?.title ?: "Событие") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit, enabled = event != null) {
                        Icon(Icons.Filled.Edit, contentDescription = "Изменить событие")
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = event != null
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Удалить событие")
                    }
                }
            )
        }
    ) { padding ->
        val current = event ?: return@Scaffold
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            current.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = current.title,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(12.dp))
            }

            Text(current.date, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Text(categoryLabel(current.category), style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(12.dp))
            Text(current.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Text("Место: ${current.place}", style = MaterialTheme.typography.bodySmall)
            Text("Важность: ${current.importance}/10", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onManagePeople) {
                Icon(Icons.Filled.Link, contentDescription = null)
                Text(" Управление связями")
            }

            if (links.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text("Связанные личности", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    links.forEach { item ->
                        Text(
                            "${item.personName} — ${roleLabel(item.role)}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { onPersonClick(item.personId) }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить событие?") },
            text = { Text("Событие и все его связи будут удалены без возможности отмены.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val current = event ?: return@TextButton
                        showDeleteDialog = false
                        scope.launch {
                            repository.deleteEvent(current)
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
