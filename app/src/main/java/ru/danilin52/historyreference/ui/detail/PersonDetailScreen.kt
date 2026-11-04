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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.CircleShape
import ru.danilin52.historyreference.data.HistoryRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    repository: HistoryRepository,
    personId: Long,
    onBack: () -> Unit,
    onEventClick: (Long) -> Unit
) {
    val person by repository.person(personId).collectAsStateWithLifecycle(initialValue = null)
    val events by repository.eventsForPerson(personId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(person?.name ?: "Личность") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
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
}