package pl.poznan.put.student.reminder.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pl.poznan.put.student.reminder.database.ReminderDao
import pl.poznan.put.student.reminder.database.entity.ReminderEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getReminders() : Flow<List<ReminderEntity>> = withContext(ioDispatcher) {
        reminderDao.getAllReminders()
    }
    suspend fun getReminder(id: Int) : ReminderEntity = withContext(ioDispatcher) {
        reminderDao.getReminderById(id)
    }
    suspend fun updateReminder(id: Int, title: String, datetime: Long, isDone: Boolean) {
        withContext(ioDispatcher) {
            reminderDao.updateReminder(id, title, datetime, isDone)
        }
    }
    suspend fun deleteAll() {
        withContext(ioDispatcher) {
            reminderDao.deleteAll()
        }
    }
    suspend fun deleteById(id: Int) {
        withContext(ioDispatcher) {
            reminderDao.deleteById(id)
        }
    }
    suspend fun getUndoneReminders() : Flow<List<ReminderEntity>> = withContext(ioDispatcher) {
        reminderDao.getUndoneReminders()
    }
    suspend fun getDoneReminders() : Flow<List<ReminderEntity>> = withContext(ioDispatcher) {
        reminderDao.getDoneReminders()
    }
    suspend fun insertReminder(reminder: ReminderEntity) {
        withContext(ioDispatcher) {
            reminderDao.insertReminder(reminder)
        }
    }
    suspend fun insertReminders(reminders: List<ReminderEntity>) {
        withContext(ioDispatcher) {
            reminderDao.insertReminders(reminders)
        }
    }
}