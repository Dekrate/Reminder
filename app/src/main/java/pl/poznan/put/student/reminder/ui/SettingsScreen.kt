package pl.poznan.put.student.reminder.ui

import android.content.Context
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
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.gson.Gson
import pl.poznan.put.student.reminder.database.entity.ReminderEntity
import pl.poznan.put.student.reminder.list.ReminderDto
import pl.poznan.put.student.reminder.viewmodel.ReminderViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun SettingsScreen() {
    val viewModel: ReminderViewModel = hiltViewModel()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var isFingerprintEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("reminder_fingerprint_enabled", false))
    }

    val getContentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(uri)
            val content = inputStream?.bufferedReader().use { it?.readText() }
            val gson = Gson()
            val reminders = gson.fromJson(content, Array<ReminderDto>::class.java)
            val existingReminders = viewModel._uiState.value.reminderDtos
            if (existingReminders.isEmpty()) {
                for (reminder in reminders) {
                    val entity = ReminderEntity().apply {
                        id = reminder.id
                        title = reminder.title
                        date = reminder.date
                        time = reminder.time
                        isDone = reminder.isDone
                    }

                    viewModel.insertReminder(entity)
                }
            } else {
                for (reminder in reminders) {
                    for (existingReminder in existingReminders) {
                        if (reminder.id == existingReminder.id) {
                            break
                        } else if (reminder.title == existingReminder.title) {
                            break
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
    }
    val biometricManager = BiometricManager.from(context)
    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        Row {
            Text("Użyj odcisku palca", modifier = Modifier.fillMaxWidth())
            Checkbox(
                checked = isFingerprintEnabled,
                // niech ten checkbox będzie po prawej stronie
                enabled = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS,
                onCheckedChange = { isChecked ->
                    isFingerprintEnabled = isChecked
                    // Zapisz nową wartość do SharedPreferences
                    sharedPreferences.edit {
                        putBoolean("reminder_fingerprint_enabled", isChecked)
                    }
                    Toast.makeText(context, isFingerprintEnabled.toString(), Toast.LENGTH_SHORT).show()
                    // wyświetl komunikat
                }


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
                if (file.exists()) {
                    file.delete()
                }
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