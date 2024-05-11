package pl.poznan.put.student.reminder.list

data class ReminderDto(
    val id: Int,
    val title: String,
    val datetime: Long,
    val isDone: Boolean
)