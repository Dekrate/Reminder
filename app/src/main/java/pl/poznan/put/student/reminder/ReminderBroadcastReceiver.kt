package pl.poznan.put.student.reminder

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val reminderText = intent.getStringExtra("REMINDER_TEXT") ?: "Przypomnienie"

        val notificationManager = NotificationManagerCompat.from(context)

        // Tworzenie kanału powiadomień
        val channel = NotificationChannel(
            "REMINDER_CHANNEL",
            "Przypomnienia",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        // Akcja otwierająca aplikację
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Budowanie powiadomienia
        val builder = NotificationCompat.Builder(context, "REMINDER_CHANNEL")
//            .setSmallIcon(R.drawable.ic_reminder) // Ikona powiadomienia
            .setContentTitle("Przypomnienie")
            .setContentText(reminderText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openAppPendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(intent.getIntExtra("REMINDER_ID", 0), builder.build())
    }
}