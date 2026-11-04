# Keep model classes used by Gson / Retrofit
-keep class ru.danilin52.historyreference.data.remote.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Room
-keep class * extends androidx.room.RoomDatabase { *; }