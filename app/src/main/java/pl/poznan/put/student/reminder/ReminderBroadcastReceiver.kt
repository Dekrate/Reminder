package pl.poznan.put.student.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderTitle = intent.getStringExtra("REMINDER_TITLE") ?: "Przypomnienie"

        val notificationManager = NotificationManagerCompat.from(context)

        // Tworzenie kanału powiadomień
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Przypomnienia",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        // Akcja otwierająca aplikację
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE)

        // Budowanie powiadomienia
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(reminderTitle)
            .setSmallIcon(R.drawable.logo)
            .setContentText("Masz nowe przypomnienie!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openAppPendingIntent)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        const val CHANNEL_ID = "reminder_channel"
        const val NOTIFICATION_ID = 1
    }
}