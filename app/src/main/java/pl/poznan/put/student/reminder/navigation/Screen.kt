package pl.poznan.put.student.reminder.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object AddReminderScreen : Screen("add_reminder_screen")
    object EditReminderScreen : Screen("edit_reminder_screen/{id}") {
        fun createRoute(id: String) = "edit_reminder_screen/${id}"
    }
    object OptionsScreen : Screen("options_screen")
}