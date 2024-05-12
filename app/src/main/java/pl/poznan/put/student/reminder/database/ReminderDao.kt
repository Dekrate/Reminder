package pl.poznan.put.student.reminder.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.poznan.put.student.reminder.database.entity.ReminderEntity

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminderentity")
    fun getAllReminders(): Flow<List<ReminderEntity>>
    @Query("SELECT * FROM reminderentity WHERE id = :id")
    fun getReminderById(id: Int): ReminderEntity
    @Query("SELECT COUNT(*) FROM reminderentity")
    suspend fun countReminders(): Int
    @Update
    fun updateReminder(reminder: ReminderEntity)
    @Query("DELETE FROM reminderentity")
    suspend fun deleteAll()
    @Query("DELETE FROM reminderentity WHERE id = :id")
    suspend fun deleteById(id: Int)
    @Query("SELECT * FROM reminderentity WHERE isDone = 0")
    fun getUndoneReminders(): Flow<List<ReminderEntity>>
    @Query("SELECT * FROM reminderentity WHERE isDone = 1")
    fun getDoneReminders(): Flow<List<ReminderEntity>>
    @Insert
    suspend fun insertReminder(reminder: ReminderEntity)
    @Insert
    suspend fun insertReminders(reminders: List<ReminderEntity>)
}
