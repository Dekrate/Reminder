package pl.poznan.put.student.reminder.list

data class ReminderDto(
    val id: Int,
    val title: String,
    val date: Long,
    val time: Long,
    val isDone: Boolean
)