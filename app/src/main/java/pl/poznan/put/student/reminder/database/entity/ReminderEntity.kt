package pl.poznan.put.student.reminder.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ReminderEntity {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
    var title: String = ""
    var datetime: Long = 0
    var isDone: Boolean = false
}