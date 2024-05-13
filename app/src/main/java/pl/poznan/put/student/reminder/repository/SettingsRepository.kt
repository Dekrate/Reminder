package pl.poznan.put.student.reminder.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pl.poznan.put.student.reminder.database.SettingsDao
import pl.poznan.put.student.reminder.database.entity.SettingsEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getSettings() : Flow<SettingsEntity> = withContext(ioDispatcher) {
        settingsDao.getSettings()
    }
    suspend fun updateSettings(settings: SettingsEntity) {
        withContext(ioDispatcher) {
            settingsDao.updateSettings(settings)
        }
    }
}