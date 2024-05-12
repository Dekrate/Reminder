package pl.poznan.put.student.reminder

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.poznan.put.student.reminder.navigation.Navigation
import pl.poznan.put.student.reminder.ui.AddReminderScreen
import pl.poznan.put.student.reminder.ui.Theme.TrailsTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrailsTheme {
                MyApp()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp() {
    // Inicjalizacja z domyślnymi wartościami, aby uniknąć NullPointerException
    val navController = rememberNavController()
    MaterialTheme {
        Scaffold(
            topBar = {
                Column {
                    Row {
                        Text(text = "Reminder", modifier = Modifier.padding(16.dp))
                        IconButton(onClick = { AddReminderScreen() }) {
                            Icon(Icons.Default.Add, contentDescription = "Dodaj przypomnienie")
                        }
                        IconButton(onClick = { TODO() }) {
                            Icon(Icons.Default.Settings, contentDescription = "Opcje")
                        }
                    }
                }
            }
        ) {
                innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Navigation(navController = navController)
            }
        }
    }
}