package ru.danilin52.historyreference.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class EventCategory { WAR, CULTURE, SCIENCE, POLITICS, EXPLORATION, OTHER }
enum class EventPersonRole { PARTICIPANT, WITNESS, INITIATOR }

@Entity(tableName = "historical_events")
data class HistoricalEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val title: String,
    val description: String,
    val category: EventCategory,
    val place: String,
    val importance: Int,
    val imageUrl: String?
)

@Entity(tableName = "persons")
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val birthYear: Int?,
    val deathYear: Int?,
    val biography: String,
    val portraitUrl: String?,
    val occupation: String
)

@Entity(
    tableName = "event_person",
    primaryKeys = ["eventId", "personId"],
    foreignKeys = [
        ForeignKey(
            entity = HistoricalEvent::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("eventId"), Index("personId")]
)
data class EventPerson(
    val eventId: Long,
    val personId: Long,
    val role: EventPersonRole
)