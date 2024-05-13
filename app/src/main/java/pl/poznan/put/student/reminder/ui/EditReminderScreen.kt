package pl.poznan.put.student.reminder.ui


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pl.poznan.put.student.reminder.database.entity.ReminderEntity
import pl.poznan.put.student.reminder.navigation.Screen
import pl.poznan.put.student.reminder.viewmodel.ReminderViewModel
import java.time.Instant
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "DiscouragedApi")
@Composable
fun EditReminderScreen(navController: NavController, reminderEntity: ReminderEntity) {
    val viewModel: ReminderViewModel = hiltViewModel()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var reminderName by remember { mutableStateOf(reminderEntity.title) }
    val dateState = rememberDatePickerState(selectableDates = FutureSelectableDates, initialSelectedDateMillis = reminderEntity.date)
    val timeState = rememberTimePickerState(initialHour = (reminderEntity.time / 3600).toInt(), initialMinute = (reminderEntity.time % 3600 / 60).toInt())
    val snackHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxHeight()
        ) {
            // nazwa przypomnienia
            OutlinedTextField(
                value = reminderName,
                onValueChange = { reminderName = it },
                label = { Text("Nazwa przypomnienia") },
                modifier = Modifier.padding(8.dp)
            )
            // data przypomnienia
            DatePicker(
                state = dateState
            )
            // czas przypomnienia
            TimePicker(
                state = timeState
            )
            // przycisk zapisu
            Button(
                onClick = {
                    reminderEntity.title = reminderName
                    val hour = timeState.hour
                    val minute = timeState.minute
                    // convert hour and minute to long
                    val time = hour * 3600 + minute * 60
                    reminderEntity.date = dateState.selectedDateMillis!!
                    reminderEntity.time = time.toLong()
                    // Sprawdź czy czas (czyli data i godzina) są w przyszłości. Jeśli nie, wyświetl komunikat.

                    // SPRAWDŹ CONSTRAINTY
                    if (reminderEntity.date < System.currentTimeMillis()) {
                        scope.launch {
                            snackHostState.showSnackbar(
                                message = "Nie można dodać przypomnienia z przeszłości"
                            )
                        }
                        return@Button
                    }
                    val instant = Instant.ofEpochMilli(reminderEntity.date)
                    if (instant.equals(LocalDate.now()) && reminderEntity.time < System.currentTimeMillis()) {
                        scope.launch {
                            snackHostState.showSnackbar(
                                message = "Nie można dodać przypomnienia z przeszłości"
                            )
                        }
                        return@Button
                    }

                    if (reminderEntity.title == "") {
                        scope.launch {
                            snackHostState.showSnackbar(
                                message = "Nie można dodać przypomnienia bez nazwy"
                            )
                        }
                        return@Button
                    }



                    viewModel.updateReminder(reminderEntity)
                    scope.launch {
                        snackHostState.showSnackbar(
                            message = "Zmodyfikowano przypomnienie"
                        )
                    }


                    navController.navigate(Screen.HomeScreen.route)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Zapisz")
            }
        }
    }
}

