package ru.danilin52.historyreference.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM historical_events ORDER BY date ASC")
    fun observeEvents(): Flow<List<HistoricalEvent>>

    @Query("SELECT * FROM historical_events WHERE id = :id")
    fun observeEvent(id: Long): Flow<HistoricalEvent?>

    @Query("SELECT * FROM persons ORDER BY name ASC")
    fun observePersons(): Flow<List<Person>>

    @Query("SELECT * FROM persons WHERE id = :id")
    fun observePerson(id: Long): Flow<Person?>

    @Query(
        "SELECT * FROM persons WHERE name LIKE '%' || :query || '%' " +
            "OR occupation LIKE '%' || :query || '%' ORDER BY name ASC"
    )
    fun searchPersons(query: String): Flow<List<Person>>

    @Query(
        "SELECT p.* FROM persons p INNER JOIN event_person ep ON p.id = ep.personId " +
            "WHERE ep.eventId = :eventId"
    )
    fun observePersonsForEvent(eventId: Long): Flow<List<Person>>

    @Query(
        "SELECT e.* FROM historical_events e INNER JOIN event_person ep ON e.id = ep.eventId " +
            "WHERE ep.personId = :personId ORDER BY e.date ASC"
    )
    fun observeEventsForPerson(personId: Long): Flow<List<HistoricalEvent>>

    @Query("SELECT COUNT(*) FROM historical_events")
    suspend fun countEvents(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<HistoricalEvent>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersons(persons: List<Person>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventPersons(links: List<EventPerson>)

    @Query("SELECT * FROM historical_events WHERE date LIKE '%' || :dayMonth")
    suspend fun eventsOnDate(dayMonth: String): List<HistoricalEvent>
}