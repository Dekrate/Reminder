package pl.poznan.put.student.reminder

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.poznan.put.student.reminder.navigation.Navigation
import pl.poznan.put.student.reminder.ui.Theme.TrailsTheme


@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isFingerprintEnabled = sharedPreferences.getBoolean("reminder_fingerprint_enabled", false)

        fun authenticateUser(onAuthenticationResult: (Boolean) -> Unit) {
            val executor = ContextCompat.getMainExecutor(this)
            val biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        onAuthenticationResult(false)
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onAuthenticationResult(true)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        onAuthenticationResult(false)
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for Reminder App")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build()

            val biometricManager = BiometricManager.from(this)
            when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    biometricPrompt.authenticate(promptInfo)
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    // In this case, fallback to another authentication method
                    onAuthenticationResult(false)
                }
            }
        }

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // get location and save it
                }
                permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                }
                else -> {
                    // No location access granted.
                }
            }
        }

        if (isFingerprintEnabled) {
            authenticateUser { isAuthenticated ->
                if (isAuthenticated) {
                    locationPermissionRequest.launch(arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                    setContent {
                        TrailsTheme {
                            MyApp()
                        }
                    }
                } else {
                    finish() // Close the app if authentication fails
                }
            }
        } else {
            // No need to authenticate, proceed directly
            locationPermissionRequest.launch(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ))
            setContent {
                TrailsTheme {
                    MyApp()
                }
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
                        val rotation =
                            remember { Animatable(0f) }
                        LaunchedEffect(true) {
                            rotation.animateTo(
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(
                                        durationMillis = 2000,
                                        easing = LinearEasing
                                    ), repeatMode = RepeatMode.Restart
                                )
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Spinning Image",
                            modifier = Modifier.graphicsLayer { rotationZ = rotation.value }.height(50.dp).width(50.dp))
                        Text(text = "Reminder", modifier = Modifier.padding(16.dp))
                        IconButton(onClick = { navController.navigate("add_reminder_screen") }) {
                            Icon(Icons.Default.Add, contentDescription = "Dodaj przypomnienie")
                        }
                        IconButton(onClick = { navController.navigate("settings_screen") }) {
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