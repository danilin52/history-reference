package ru.danilin52.historyreference.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: HistoryDao) {

    val events: Flow<List<HistoricalEvent>> = dao.observeEvents()
    val persons: Flow<List<Person>> = dao.observePersons()

    fun event(id: Long): Flow<HistoricalEvent?> = dao.observeEvent(id)
    fun person(id: Long): Flow<Person?> = dao.observePerson(id)
    fun personsForEvent(eventId: Long): Flow<List<Person>> = dao.observePersonsForEvent(eventId)
    fun eventsForPerson(personId: Long): Flow<List<HistoricalEvent>> = dao.observeEventsForPerson(personId)
    fun searchPersons(query: String): Flow<List<Person>> = dao.searchPersons(query)

    suspend fun eventsOnDate(dayMonth: String): List<HistoricalEvent> =
        dao.eventsOnDate(dayMonth)

    suspend fun ensureSeeded() {
        if (dao.countEvents() > 0) return

        val events = SampleData.events
        dao.insertEvents(events)
        dao.insertPersons(SampleData.persons)

        val byTitle = events.associateBy { it.title }
        val byName = SampleData.persons.associateBy { it.name }

        val links = SampleData.links.mapNotNull { (eventTitle, personName, role) ->
            val event = byTitle[eventTitle] ?: return@mapNotNull null
            val person = byName[personName] ?: return@mapNotNull null
            EventPerson(eventId = event.id, personId = person.id, role = role)
        }
        dao.insertEventPersons(links)
    }
}