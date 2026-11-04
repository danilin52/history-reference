package ru.danilin52.historyreference.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import ru.danilin52.historyreference.data.EventCategory
import ru.danilin52.historyreference.data.HistoricalEvent

@Composable
fun RotatingEventCard(
    event: HistoricalEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember(event.id) { mutableStateOf(false) }
    LaunchedEffect(event.id) { visible = true }

    val rotation by animateFloatAsState(
        targetValue = if (visible) 0f else 120f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "cardRotation"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationZ = rotation
                this.alpha = alpha
            }
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(event.date, style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            Text(event.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(categoryLabel(event.category), style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(6.dp))
            Text(
                event.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Важность: ${event.importance}/10 · ${event.place}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

fun categoryLabel(category: EventCategory): String = when (category) {
    EventCategory.WAR -> "Война"
    EventCategory.CULTURE -> "Культура"
    EventCategory.SCIENCE -> "Наука"
    EventCategory.POLITICS -> "Политика"
    EventCategory.EXPLORATION -> "География"
    EventCategory.OTHER -> "Другое"
}