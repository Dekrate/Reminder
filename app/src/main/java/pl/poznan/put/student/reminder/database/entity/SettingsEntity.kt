package pl.poznan.put.student.reminder.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SettingsEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var fingerprint: Boolean = false
}