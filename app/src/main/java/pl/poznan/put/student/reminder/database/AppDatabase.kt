package pl.poznan.put.student.reminder.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.poznan.put.student.reminder.database.entity.ReminderEntity

@Database(entities = [ReminderEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}