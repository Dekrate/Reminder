package pl.poznan.put.student.reminder.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.poznan.put.student.reminder.database.entity.ReminderEntity
import pl.poznan.put.student.reminder.database.entity.SettingsEntity

@Database(entities = [ReminderEntity::class, SettingsEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun settingsDao(): SettingsDao
    // sharedpreferences
    // mapa
    // cykl życia aplikacji
}