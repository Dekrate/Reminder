package pl.poznan.put.student.reminder.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.poznan.put.student.reminder.database.entity.SettingsEntity

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settingsentity WHERE id = 1")
    fun getSettings(): Flow<SettingsEntity>
    @Update
    suspend fun updateSettings(settings: SettingsEntity) : Int
}
