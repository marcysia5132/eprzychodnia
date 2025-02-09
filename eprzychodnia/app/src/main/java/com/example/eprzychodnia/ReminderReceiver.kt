package com.example.eprzychodnia

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val channelId = "appointment_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tworzenie kanału powiadomień (wymagane dla Androida 8.0 i nowszych)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Wizyty",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Powiadomienia o wizytach"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Pobierz dane z intentu
        val lekarz = intent.getStringExtra("lekarz")
        val dataWizyty = intent.getStringExtra("data_wizyty")

        // Tworzenie powiadomienia
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Przypomnienie o wizycie")
            .setContentText("Jutro wizyta u $lekarz na $dataWizyty.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Wyświetlanie powiadomienia
        notificationManager.notify(2, notification) // Użyj innego ID niż poprzednie powiadomienie
    }
}