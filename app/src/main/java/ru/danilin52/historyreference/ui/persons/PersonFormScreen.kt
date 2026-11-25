package ru.danilin52.historyreference.ui.persons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import ru.danilin52.historyreference.data.HistoryRepository
import ru.danilin52.historyreference.data.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonFormScreen(
    repository: HistoryRepository,
    personId: Long?,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit
) {
    val personFlow = remember(personId) {
        personId?.let(repository::person) ?: flowOf(null)
    }
    val existingPerson by personFlow.collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()

    var initialized by remember(personId) { mutableStateOf(personId == null) }
    var name by remember(personId) { mutableStateOf("") }
    var birthYearText by remember(personId) { mutableStateOf("") }
    var deathYearText by remember(personId) { mutableStateOf("") }
    var biography by remember(personId) { mutableStateOf("") }
    var portraitUrl by remember(personId) { mutableStateOf("") }
    var occupation by remember(personId) { mutableStateOf("") }

    LaunchedEffect(existingPerson) {
        val person = existingPerson ?: return@LaunchedEffect
        if (!initialized) {
            name = person.name
            birthYearText = person.birthYear?.toString().orEmpty()
            deathYearText = person.deathYear?.toString().orEmpty()
            biography = person.biography
            portraitUrl = person.portraitUrl.orEmpty()
            occupation = person.occupation
            initialized = true
        }
    }

    val birthYear = birthYearText.toIntOrNull()
    val deathYear = deathYearText.toIntOrNull()
    val yearsAreValid = (birthYearText.isBlank() || birthYear != null) &&
        (deathYearText.isBlank() || deathYear != null) &&
        (birthYear == null || deathYear == null || deathYear >= birthYear)
    val formIsValid = initialized && name.isNotBlank() &&
        biography.isNotBlank() && occupation.isNotBlank() && yearsAreValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (personId == null) "Новая личность" else "Изменить личность") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    TextButton(
                        enabled = formIsValid,
                        onClick = {
                            val value = Person(
                                id = existingPerson?.id ?: 0,
                                name = name.trim(),
                                birthYear = birthYear,
                                deathYear = deathYear,
                                biography = biography.trim(),
                                portraitUrl = portraitUrl.trim().ifBlank { null },
                                occupation = occupation.trim()
                            )
                            scope.launch {
                                val savedId = if (personId == null) {
                                    repository.createPerson(value)
                                } else {
                                    repository.updatePerson(value)
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
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Имя") },
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = occupation,
                    onValueChange = { occupation = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Род деятельности") },
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = birthYearText,
                    onValueChange = { birthYearText = it.filter(Char::isDigit).take(4) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Год рождения (необязательно)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = deathYearText,
                    onValueChange = { deathYearText = it.filter(Char::isDigit).take(4) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Год смерти (необязательно)") },
                    supportingText = {
                        if (birthYear != null && deathYear != null && deathYear < birthYear) {
                            Text("Год смерти не может быть раньше года рождения")
                        }
                    },
                    isError = birthYear != null && deathYear != null && deathYear < birthYear,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = biography,
                    onValueChange = { biography = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Биография") },
                    minLines = 4
                )
            }
            item {
                OutlinedTextField(
                    value = portraitUrl,
                    onValueChange = { portraitUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("URL портрета (необязательно)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    singleLine = true
                )
            }
        }
    }
}
