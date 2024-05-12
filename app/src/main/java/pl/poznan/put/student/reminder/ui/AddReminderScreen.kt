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
import pl.poznan.put.student.reminder.database.entity.ReminderEntity
import pl.poznan.put.student.reminder.navigation.Screen
import pl.poznan.put.student.reminder.viewmodel.ReminderViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "DiscouragedApi")
@Composable
fun AddReminderScreen(navController: NavController) {
    val viewModel: ReminderViewModel = hiltViewModel()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var reminderName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var selectedTime by remember { mutableStateOf(Date()) }
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
                state = rememberDatePickerState()

            )
            // czas przypomnienia
            TimePicker(
                state = rememberTimePickerState()
            )
            // przycisk zapisu
            Button(
                onClick = {
//                    viewModel.addReminder(reminderEntity)
                    navController.navigate(Screen.HomeScreen.route)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Zapisz")
            }
        }
    }
}

fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}

