package pl.poznan.put.student.reminder.ui

import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import pl.poznan.put.student.reminder.MainActivity
import pl.poznan.put.student.reminder.database.entity.ReminderEntity
import pl.poznan.put.student.reminder.database.entity.SettingsEntity
import pl.poznan.put.student.reminder.list.ReminderDto
import pl.poznan.put.student.reminder.viewmodel.ReminderViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel: ReminderViewModel = hiltViewModel()
    val state = viewModel.uiState.collectAsState(initial = ReminderViewModel.State.DEFAULT)
    val settingsDto = state.value.settingsDto
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    val getContentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(uri)
            val content = inputStream?.bufferedReader().use { it?.readText() }
            val gson = Gson()
            val reminders = gson.fromJson(content, Array<ReminderDto>::class.java)
            val existingReminders = viewModel._uiState.value.reminderDtos
            for (reminder in reminders) {
                for (existingReminder in existingReminders) {
                    if (reminder.id == existingReminder.id) {
                        continue
                    } else if (reminder.title == existingReminder.title) {
                        continue
                    } else {
                        val entity = ReminderEntity().apply {
                            id = reminder.id
                            title = reminder.title
                            date = reminder.date
                            time = reminder.time
                            isDone = reminder.isDone
                        }

                        viewModel.insertReminder(entity)
                    }
                }
            }

        }
    }
    val biometricManager = BiometricManager.from(context)
    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        Row {
            Text("Użyj odcisku palca", modifier = Modifier.fillMaxWidth())
            Checkbox(
                checked = settingsDto.fingerprint,
                // niech ten checkbox będzie po prawej stronie
                enabled = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS,
                onCheckedChange = {
                    settingsDto.fingerprint = !settingsDto.fingerprint
                    viewModel.updateSettings(SettingsEntity().apply { fingerprint = !settingsDto.fingerprint }) },
            )

        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                // każ użytkownikowi wybrać plik z którego chce importować

                getContentLauncher.launch("application/json")




            },
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)) {
                Text("Importuj")
            }
            Button(onClick = {
                // get all data from db
                val reminders = viewModel._uiState.value.reminderDtos
                // convert to json using Gson
                val gson = Gson()
                val json = gson.toJson(reminders)
                // zapisz go do pliku w folderze documents
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "reminders.json")
                FileOutputStream(file).use {
                    it.write(json.toByteArray())
                }

                Toast.makeText(context, "Zapisano do pliku w Dokumentach", Toast.LENGTH_SHORT).show()

            },
                // połowa ekranu
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)) {
                Text("Eksportuj")
            }
        }
    }
}