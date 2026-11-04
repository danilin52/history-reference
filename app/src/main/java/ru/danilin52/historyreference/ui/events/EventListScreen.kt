package ru.danilin52.historyreference.ui.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.danilin52.historyreference.data.HistoryRepository
import ru.danilin52.historyreference.ui.components.RotatingEventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    repository: HistoryRepository,
    onEventClick: (Long) -> Unit
) {
    val events by repository.events.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("События") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events, key = { it.id }) { event ->
                RotatingEventCard(
                    event = event,
                    onClick = { onEventClick(event.id) }
                )
            }
        }
    }
}