package pl.poznan.put.student.reminder.ui

import android.content.pm.PackageManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import pl.poznan.put.student.reminder.MainActivity
import pl.poznan.put.student.reminder.database.entity.ReminderEntity
import pl.poznan.put.student.reminder.list.ReminderDto
import pl.poznan.put.student.reminder.model.RetrofitClient
import pl.poznan.put.student.reminder.model.Weather
import pl.poznan.put.student.reminder.model.WeatherApiService
import pl.poznan.put.student.reminder.viewmodel.ReminderViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: ReminderViewModel = hiltViewModel()
    val state = viewModel.uiState.collectAsState(initial = ReminderViewModel.State.DEFAULT)
    var searchText by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Szukaj po nazwie") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // List of tiles
        LazyColumn(modifier = Modifier.weight(1f)) {
            val reminders = state.value.reminderDtos.filter { it.title.contains(searchText, ignoreCase = true) }
            items(reminders.size) { index ->
                ReminderTile(navController, reminders[index])
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReminderTile(navController: NavController, reminderDto: ReminderDto) {
    val viewModel: ReminderViewModel = hiltViewModel()
    val activity: MainActivity = LocalContext.current as MainActivity
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .fillMaxSize()
            .combinedClickable(
                enabled = true,
                onClick = {
                    navController.navigate("edit_reminder_screen/${reminderDto.id}")
                },
                onLongClick = {
                    viewModel.deleteById(reminderDto.id)
                    // odśwież listę przypomnień
                    navController.navigate("home_screen")
                })
    ) {

        Row {
            Text(
                text = reminderDto.title,
                modifier = Modifier.padding(8.dp),
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formatDate(reminderDto.date) + " " + formatTime(reminderDto.time),
                modifier = Modifier.padding(8.dp)
            )
            // "ptaszek" - ukończ przypomnienie
            Checkbox(
                checked = reminderDto.isDone,
                onCheckedChange = {
                    var reminderEntity = ReminderEntity()
                    reminderEntity.id = reminderDto.id
                    reminderEntity.title = reminderDto.title
                    reminderEntity.date = reminderDto.date
                    reminderEntity.time = reminderDto.time
                    reminderEntity.isDone = !reminderDto.isDone

                    viewModel.updateReminder(reminderEntity)
                    // odśwież listę przypomnień
                    navController.navigate("home_screen")
                    },
                modifier = Modifier.padding(8.dp)
            )
            // check if location permission is granted
            if (LocalContext.current.
                checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                val result = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token,
                )
//                result.let { fetchedLocation ->
//                    longitude = fetchedLocation.result.longitude
//                    latitude = fetchedLocation.result.latitude
//                }

                longitude = 13.41
                latitude = 52.52

//                 get weather from api
//                 map date long to date object
                val dateMapped: LocalDate = Instant.ofEpochMilli(reminderDto.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val timeMapped: LocalTime = Instant.ofEpochMilli(reminderDto.time)
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                val dateTime = LocalDateTime.of(dateMapped, timeMapped)
//                 check if the date is < 7 days
                if (abs(dateTime.compareTo(LocalDateTime.now())) < 7) {
                    val temperature = fetchWeatherData(latitude, longitude, dateMapped)
                    Text(
                        text = "Temperatura: $temperature",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

fun formatDate(date: Long) : String {
    val instant = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
    val formatted = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(instant)
    return formatted
}

fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}

fun fetchWeatherData(latitude: Double, longitude: Double, localDate: LocalDate): Double {
    val weatherApiService: WeatherApiService by lazy {
        RetrofitClient.instance.create(WeatherApiService::class.java)
    }
    var temperature: Double = -273.15
    val call = weatherApiService.getWeather(latitude, longitude)
    call.enqueue(object : Callback<Weather> {
        override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
            if (response.isSuccessful) {
                val weather = response.body()

                val temperatures = weather?.daily?.temperature2mMax
                // calculate days from today
                val days = ChronoUnit.DAYS.between(LocalDate.now(), localDate)
                temperature = temperatures?.get(days.toInt() - 1)!!
            }
        }

        override fun onFailure(p0: Call<Weather>, p1: Throwable) {

        }
    })
    return temperature
}