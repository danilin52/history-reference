package ru.danilin52.historyreference.ui.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import ru.danilin52.historyreference.data.EventCategory
import ru.danilin52.historyreference.data.HistoricalEvent
import ru.danilin52.historyreference.data.HistoryRepository
import ru.danilin52.historyreference.ui.components.categoryLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen(
    repository: HistoryRepository,
    eventId: Long?,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit
) {
    val eventFlow = remember(eventId) {
        eventId?.let(repository::event) ?: flowOf(null)
    }
    val existingEvent by eventFlow.collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()

    var initialized by remember(eventId) { mutableStateOf(eventId == null) }
    var date by remember(eventId) { mutableStateOf("") }
    var title by remember(eventId) { mutableStateOf("") }
    var description by remember(eventId) { mutableStateOf("") }
    var category by remember(eventId) { mutableStateOf(EventCategory.OTHER) }
    var place by remember(eventId) { mutableStateOf("") }
    var importanceText by remember(eventId) { mutableStateOf("5") }
    var imageUrl by remember(eventId) { mutableStateOf("") }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(existingEvent) {
        val event = existingEvent ?: return@LaunchedEffect
        if (!initialized) {
            date = event.date
            title = event.title
            description = event.description
            category = event.category
            place = event.place
            importanceText = event.importance.toString()
            imageUrl = event.imageUrl.orEmpty()
            initialized = true
        }
    }

    val importance = importanceText.toIntOrNull()
    val validDate = DATE_PATTERN.matches(date.trim())
    val formIsValid = initialized &&
        validDate &&
        title.isNotBlank() &&
        description.isNotBlank() &&
        place.isNotBlank() &&
        importance in 1..10

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (eventId == null) "Новое событие" else "Изменить событие") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    TextButton(
                        enabled = formIsValid,
                        onClick = {
                            val value = HistoricalEvent(
                                id = existingEvent?.id ?: 0,
                                date = date.trim(),
                                title = title.trim(),
                                description = description.trim(),
                                category = category,
                                place = place.trim(),
                                importance = requireNotNull(importance),
                                imageUrl = imageUrl.trim().ifBlank { null }
                            )
                            scope.launch {
                                val savedId = if (eventId == null) {
                                    repository.createEvent(value)
                                } else {
                                    repository.updateEvent(value)
                                    value.id
                                }
                                onSaved(savedId)
                            }
                        }
                    ) {
                        Text("Сохранить")
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
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Дата") },
                    placeholder = { Text("ГГГГ-ММ-ДД") },
                    supportingText = {
                        if (date.isNotEmpty() && !validDate) {
                            Text("Формат даты: ГГГГ-ММ-ДД")
                        }
                    },
                    isError = date.isNotEmpty() && !validDate,
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Название") },
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Описание") },
                    minLines = 4
                )
            }
            item {
                Box {
                    OutlinedButton(onClick = { categoryMenuExpanded = true }) {
                        Text("Категория: ${categoryLabel(category)}")
                    }
                    DropdownMenu(
                        expanded = categoryMenuExpanded,
                        onDismissRequest = { categoryMenuExpanded = false }
                    ) {
                        EventCategory.entries.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(categoryLabel(item)) },
                                onClick = {
                                    category = item
                                    categoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = place,
                    onValueChange = { place = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Место") },
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = importanceText,
                    onValueChange = { importanceText = it.filter(Char::isDigit).take(2) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Важность (1–10)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = importanceText.isNotEmpty() && importance !in 1..10,
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("URL изображения (необязательно)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    singleLine = true
                )
            }
        }
    }
}

private val DATE_PATTERN = Regex("\\d{4}-\\d{2}-\\d{2}")
