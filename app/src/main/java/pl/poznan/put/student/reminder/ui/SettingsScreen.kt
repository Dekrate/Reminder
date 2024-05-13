package pl.poznan.put.student.reminder.ui

import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
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
import pl.poznan.put.student.reminder.database.entity.SettingsEntity
import pl.poznan.put.student.reminder.viewmodel.ReminderViewModel

@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel: ReminderViewModel = hiltViewModel()
    val state = viewModel.uiState.collectAsState(initial = ReminderViewModel.State.DEFAULT)
    val settingsDto = state.value.settingsDto
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
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
            Button(onClick = { /*TODO*/ },
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically)) {
                Text("Importuj")
            }
            Button(onClick = { /*TODO*/ },
                // połowa ekranu
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically)) {
                Text("Eksportuj")
            }
        }
    }
}