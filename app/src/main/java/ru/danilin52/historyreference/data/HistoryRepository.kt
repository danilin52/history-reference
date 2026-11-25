package ru.danilin52.historyreference.data

import kotlinx.coroutines.flow.Flow
import ru.danilin52.historyreference.preferences.UserPreferences

class HistoryRepository(
    private val dao: HistoryDao,
    private val preferences: UserPreferences
) {

    val events: Flow<List<HistoricalEvent>> = dao.observeEvents()
    val persons: Flow<List<Person>> = dao.observePersons()

    fun event(id: Long): Flow<HistoricalEvent?> = dao.observeEvent(id)
    fun person(id: Long): Flow<Person?> = dao.observePerson(id)
    fun personsForEvent(eventId: Long): Flow<List<Person>> = dao.observePersonsForEvent(eventId)
    fun eventsForPerson(personId: Long): Flow<List<HistoricalEvent>> = dao.observeEventsForPerson(personId)
    fun searchPersons(query: String): Flow<List<Person>> = dao.searchPersons(query)
    fun eventPersonItems(eventId: Long): Flow<List<EventPersonItem>> =
        dao.observeEventPersonItems(eventId)

    suspend fun createEvent(event: HistoricalEvent): Long = dao.insertEvent(event)
    suspend fun updateEvent(event: HistoricalEvent) = dao.updateEvent(event)
    suspend fun deleteEvent(event: HistoricalEvent) = dao.deleteEvent(event)

    suspend fun createPerson(person: Person): Long = dao.insertPerson(person)
    suspend fun updatePerson(person: Person) = dao.updatePerson(person)
    suspend fun deletePerson(person: Person) = dao.deletePerson(person)

    suspend fun saveEventPerson(link: EventPerson) = dao.upsertEventPerson(link)
    suspend fun updateEventPerson(link: EventPerson) = dao.updateEventPerson(link)
    suspend fun deleteEventPerson(link: EventPerson) = dao.deleteEventPerson(link)

    suspend fun eventsOnDate(dayMonth: String): List<HistoricalEvent> =
        dao.eventsOnDate(dayMonth)

    suspend fun ensureSeeded() {
        if (preferences.databaseSeeded) return

        if (dao.countEvents() > 0) {
            preferences.databaseSeeded = true
            return
        }

        val events = SampleData.events
        val byTitle = events.associateBy { it.title }
        val byName = SampleData.persons.associateBy { it.name }

        val links = SampleData.links.mapNotNull { (eventTitle, personName, role) ->
            val event = byTitle[eventTitle] ?: return@mapNotNull null
            val person = byName[personName] ?: return@mapNotNull null
            EventPerson(eventId = event.id, personId = person.id, role = role)
        }
        dao.seed(events, SampleData.persons, links)
        preferences.databaseSeeded = true
    }
}
