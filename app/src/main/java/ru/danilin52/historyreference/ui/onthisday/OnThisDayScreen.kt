package ru.danilin52.historyreference.ui.onthisday

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.danilin52.historyreference.data.HistoricalEvent
import ru.danilin52.historyreference.data.HistoryRepository
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnThisDayScreen(
    repository: HistoryRepository,
    onEventClick: (Long) -> Unit
) {
    val today = remember { LocalDate.now() }
    val dayMonth = remember(today) {
        "%02d-%02d".format(today.monthValue, today.dayOfMonth)
    }
    var events by remember { mutableStateOf(emptyList<HistoricalEvent>()) }

    LaunchedEffect(dayMonth) {
        events = repository.eventsOnDate(dayMonth)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("В этот день") })
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
                Text(
                    "Сегодня: $dayMonth",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            if (events.isEmpty()) {
                item {
                    Text(
                        "Пока нет событий на эту дату — загляни позже.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            items(events, key = { it.id }) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEventClick(event.id) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(event.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            event.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}