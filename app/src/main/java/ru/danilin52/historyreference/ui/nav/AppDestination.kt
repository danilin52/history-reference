package ru.danilin52.historyreference.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

object AppRoutes {
    const val EVENTS = "events"
    const val PERSONS = "persons"
    const val ON_THIS_DAY = "on_this_day"
    const val SETTINGS = "settings"

    const val EVENT_ID = "eventId"
    const val PERSON_ID = "personId"
    const val EVENT_CREATE = "event/new"
    const val EVENT_DETAIL = "event/{$EVENT_ID}"
    const val EVENT_EDIT = "event/{$EVENT_ID}/edit"
    const val EVENT_PEOPLE = "event/{$EVENT_ID}/people"
    const val PERSON_CREATE = "person/new"
    const val PERSON_DETAIL = "person/{$PERSON_ID}"
    const val PERSON_EDIT = "person/{$PERSON_ID}/edit"

    fun eventDetail(eventId: Long): String = "event/$eventId"
    fun eventEdit(eventId: Long): String = "event/$eventId/edit"
    fun eventPeople(eventId: Long): String = "event/$eventId/people"
    fun personDetail(personId: Long): String = "person/$personId"
    fun personEdit(personId: Long): String = "person/$personId/edit"
}

val topLevelDestinations = listOf(
    TopLevelDestination(AppRoutes.EVENTS, "События", Icons.Filled.History),
    TopLevelDestination(AppRoutes.PERSONS, "Личности", Icons.Filled.Person),
    TopLevelDestination(AppRoutes.ON_THIS_DAY, "В этот день", Icons.Filled.Notifications),
    TopLevelDestination(AppRoutes.SETTINGS, "Настройки", Icons.Filled.Settings)
)
