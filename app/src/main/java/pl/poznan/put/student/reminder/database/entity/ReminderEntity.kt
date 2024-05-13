package pl.poznan.put.student.reminder.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ReminderEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var title: String = ""
    var date: Long = 0
    var time: Long = 0
    var isDone: Boolean = false
}
