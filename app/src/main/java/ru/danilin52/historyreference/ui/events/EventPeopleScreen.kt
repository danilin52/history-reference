package ru.danilin52.historyreference.ui.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.danilin52.historyreference.data.EventPerson
import ru.danilin52.historyreference.data.EventPersonRole
import ru.danilin52.historyreference.data.HistoryRepository
import ru.danilin52.historyreference.ui.components.roleLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventPeopleScreen(
    repository: HistoryRepository,
    eventId: Long,
    onBack: () -> Unit
) {
    val event by repository.event(eventId).collectAsStateWithLifecycle(initialValue = null)
    val persons by repository.persons.collectAsStateWithLifecycle(initialValue = emptyList())
    val links by repository.eventPersonItems(eventId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()

    var selectedPersonId by remember { mutableStateOf<Long?>(null) }
    var selectedRole by remember { mutableStateOf(EventPersonRole.PARTICIPANT) }
    var personMenuExpanded by remember { mutableStateOf(false) }
    var roleMenuExpanded by remember { mutableStateOf(false) }

    val selectedPerson = persons.firstOrNull { it.id == selectedPersonId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event?.let { "Связи: ${it.title}" } ?: "Связи события") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Добавление и изменение связи", style = MaterialTheme.typography.titleMedium)
            }
            item {
                Box {
                    OutlinedButton(
                        onClick = { personMenuExpanded = true },
                        enabled = persons.isNotEmpty()
                    ) {
                        Text(selectedPerson?.name ?: "Выберите личность")
                    }
                    DropdownMenu(
                        expanded = personMenuExpanded,
                        onDismissRequest = { personMenuExpanded = false }
                    ) {
                        persons.forEach { person ->
                            DropdownMenuItem(
                                text = { Text(person.name) },
                                onClick = {
                                    selectedPersonId = person.id
                                    personMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                Box {
                    OutlinedButton(onClick = { roleMenuExpanded = true }) {
                        Text("Роль: ${roleLabel(selectedRole)}")
                    }
                    DropdownMenu(
                        expanded = roleMenuExpanded,
                        onDismissRequest = { roleMenuExpanded = false }
                    ) {
                        EventPersonRole.entries.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(roleLabel(role)) },
                                onClick = {
                                    selectedRole = role
                                    roleMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                Button(
                    enabled = selectedPersonId != null,
                    onClick = {
                        val personId = selectedPersonId ?: return@Button
                        scope.launch {
                            val link = EventPerson(eventId, personId, selectedRole)
                            if (links.any { it.personId == personId }) {
                                repository.updateEventPerson(link)
                            } else {
                                repository.saveEventPerson(link)
                            }
                            selectedPersonId = null
                            selectedRole = EventPersonRole.PARTICIPANT
                        }
                    }
                ) {
                    Text("Сохранить связь")
                }
            }

            item {
                Text("Связанные личности", style = MaterialTheme.typography.titleMedium)
            }

            if (links.isEmpty()) {
                item { Text("Связей пока нет") }
            }

            items(links, key = { "${it.eventId}-${it.personId}" }) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.personName, style = MaterialTheme.typography.titleSmall)
                            Text(roleLabel(item.role), style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(
                            onClick = {
                                selectedPersonId = item.personId
                                selectedRole = item.role
                            }
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Изменить связь")
                        }
                        IconButton(
                            onClick = {
                                scope.launch {
                                    repository.deleteEventPerson(
                                        EventPerson(item.eventId, item.personId, item.role)
                                    )
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Удалить связь")
                        }
                    }
                }
            }
        }
    }
}
