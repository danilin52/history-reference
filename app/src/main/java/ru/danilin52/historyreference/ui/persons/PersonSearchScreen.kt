package ru.danilin52.historyreference.ui.persons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.danilin52.historyreference.data.HistoryRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonSearchScreen(
    repository: HistoryRepository,
    onPersonClick: (Long) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val results by remember(query) { repository.searchPersons(query) }
        .collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Личности") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Имя или род деятельности") },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(results, key = { it.id }) { person ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPersonClick(person.id) }
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(person.name, style = MaterialTheme.typography.titleMedium)
                            Text(person.occupation, style = MaterialTheme.typography.bodySmall)
                            Text(
                                "${person.birthYear ?: "?"} — ${person.deathYear ?: "?"}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}