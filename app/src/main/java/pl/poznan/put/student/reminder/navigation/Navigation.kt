package pl.poznan.put.student.reminder.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import pl.poznan.put.student.reminder.viewmodel.ReminderViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pl.poznan.put.student.reminder.ui.*

@Composable
fun Navigation(navController: NavHostController) {

    val viewModel: ReminderViewModel = hiltViewModel()
    val configuration = LocalConfiguration.current
    Box {
        NavHost(navController = navController, startDestination = "home_screen") {
            composable(
                route = "home_screen"
            ) {
                HomeScreen(navController)
            }
            composable(
                route = "add_reminder_screen"
            ) {
                AddReminderScreen(navController)
            }
            composable(
                route = "splash_screen"
            ) {
                SpinningImage()
            }
            composable(
                route = "edit_reminder_screen/{id}"
            ) { backStackEntry ->
                val reminderId = backStackEntry.arguments?.getString("id")?.toInt()
                reminderId?.let { viewModel.getReminderById(it) }
                val selectedReminderState =
                    viewModel.uiState.collectAsState(ReminderViewModel.State.DEFAULT).value.selectedReminder

                selectedReminderState?.let { selectedReminder ->
                EditReminderScreen(navController = navController, reminderEntity = selectedReminder)
                }
            }
            composable(
                route = "settings_screen"
            ) {
                SettingsScreen(navController)
            }

        }
    }
    }