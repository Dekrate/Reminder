package pl.poznan.put.student.reminder.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
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
import pl.poznan.put.student.reminder.ReminderBroadcastReceiver
import pl.poznan.put.student.reminder.database.entity.ReminderEntity
import pl.poznan.put.student.reminder.navigation.Screen
import pl.poznan.put.student.reminder.viewmodel.ReminderViewModel
import java.time.Instant
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "DiscouragedApi")
@Composable
fun AddReminderScreen(navController: NavController) {
    val viewModel: ReminderViewModel = hiltViewModel()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var reminderName by remember { mutableStateOf("") }
//    val dateState = rememberDatePickerState(selectableDates = FutureSelectableDates)
    val dateState = rememberDatePickerState()
    val timeState = rememberTimePickerState()
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
                    val reminderEntity = ReminderEntity()
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



                    viewModel.insertReminder(reminderEntity)
                    scope.launch {
                        snackHostState.showSnackbar(
                            message = "Dodano przypomnienie"
                        )
                    }
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(context, ReminderBroadcastReceiver::class.java)
                    intent.putExtra("REMINDER_TITLE", reminderEntity.title) // Dodaj nazwę przypomnienia do Intent
                    val pendingIntent = PendingIntent.getBroadcast(context, reminderEntity.id, intent, PendingIntent.FLAG_IMMUTABLE)

                    val triggerAtMillis = reminderEntity.date + reminderEntity.time * 1000 - (3600*1000*2)
                    if (alarmManager.canScheduleExactAlarms())
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)

                    navController.navigate(Screen.HomeScreen.route)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Zapisz")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
object FutureSelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= System.currentTimeMillis()
        }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= LocalDate.now().year
    }
}

