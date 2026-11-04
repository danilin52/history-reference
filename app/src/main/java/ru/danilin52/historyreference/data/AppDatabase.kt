package ru.danilin52.historyreference.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun categoryToString(value: EventCategory): String = value.name

    @TypeConverter
    fun stringToCategory(value: String): EventCategory =
        runCatching { EventCategory.valueOf(value) }.getOrDefault(EventCategory.OTHER)

    @TypeConverter
    fun roleToString(value: EventPersonRole): String = value.name

    @TypeConverter
    fun stringToRole(value: String): EventPersonRole =
        runCatching { EventPersonRole.valueOf(value) }.getOrDefault(EventPersonRole.PARTICIPANT)
}

@Database(
    entities = [HistoricalEvent::class, Person::class, EventPerson::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "history.db"
            ).build()
    }
}