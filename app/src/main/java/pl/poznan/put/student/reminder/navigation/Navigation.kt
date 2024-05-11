package pl.poznan.put.student.reminder.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import pl.poznan.put.student.reminder.viewmodel.ReminderViewModel

@Composable
fun Navigation(navController: NavHostController) {

    val viewModel: ReminderViewModel = hiltViewModel()
    val configuration = LocalConfiguration.current
    Box {
        NavHost(navController = navController, startDestination = "home_screen") {
            composable(
                route = "list_screen"
            ) {
                if(configuration.screenWidthDp < 600) {
                    TrailScreen(navController)
                } else {
                    TrailTabletScreen(navController)
                }
            }
            composable(
                route = "list_alternative_screen"
            ) {
                if(configuration.screenWidthDp < 600) {
                    TrailAlternativeScreen(navController)
                } else {
                    TrailTabletAlternativeScreen(navController = navController)
                }
            }
            composable(
                route = "start_screen"
            ) {
                StartScreen()
            }
            composable(
                route = "splash_screen"
            ) {
                SpinningImage()
            }
            composable(
                route = "details_screen/{id}"
            ) { backStackEntry ->
                val trailId = backStackEntry.arguments?.getString("id")?.toInt()
                trailId?.let { viewModel.getTrailById(it) }
                val selectedTrailState =
                    viewModel.uiState.collectAsState(TrailViewModel.State.DEFAULT).value.selectedTrail

                selectedTrailState?.let { selectedTrail ->
                    DetailsScreen(navController = navController, trailEntity = selectedTrail)
                }
            }
        }
    }