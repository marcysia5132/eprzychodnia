package com.example.eprzychodnia

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.example.eprzychodnia.PacWizyty.Companion.selectedVisitDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PacPotwWizyty : AppCompatActivity() {
    private lateinit var confrimButton: Button
    val id_doctor = Lista_lekarzy.selectedDoctorId
    val id_pacjenta = MainActivity.userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pac_potw_wizyty)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val Wybrana_data_wizyty = PacWizyty.selectedVisitDate
        val Wybrany_lekarz = Lista_lekarzy.NaszLekarz

        val SzczegolywizytyTextView = findViewById<TextView>(R.id.PacPotwWizyty_szczegoly)
        SzczegolywizytyTextView.text = "Wizyta zaplanowana na: $Wybrana_data_wizyty"
        val WizytaLekarzTextView = findViewById<TextView>(R.id.PacPotwWizyty_lekarz)
        WizytaLekarzTextView.text = "Lekarz przyjmujący: $Wybrany_lekarz"

        confrimButton = findViewById(R.id.PacPotwWizte_przycisk)
        confrimButton.visibility = Button.GONE
        if (isAppointmentCancelable(Wybrana_data_wizyty)) {
            confrimButton.visibility = Button.VISIBLE
        }

        confrimButton.setOnClickListener {
            MainActivity0.Pomoc = 0
            saveAppointment()
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }
    }

    private fun saveAppointment() {
        val url = getString(R.string.db_url_xampp) + "save_appointment.php"

        val params = HashMap<String, String>()
        params["doctor_id"] = id_doctor.toString()
        params["patient_id"] = id_pacjenta.toString()
        params["date"] = selectedVisitDate

        Log.d("SaveAppointment", "Wysyłane dane: $params") // <-- SPRAWDZAMY DANE

        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Log.d("SaveAppointment", "Odpowiedź z serwera: $response")
                Toast.makeText(this, "Wizyta zapisana!", Toast.LENGTH_SHORT).show()

                // Wywołanie powiadomienia po zapisaniu wizyty
                showAppointmentNotification()
            },
            Response.ErrorListener { error ->
                Log.e("SaveAppointment", "Błąd przy zapisie wizyty: $error")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return params
            }
        }

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun showAppointmentNotification() {
        // Tworzenie kanału powiadomień (wymagane dla Androida 8.0 i nowszych)
        val channelId = "appointment_channel"
        val channelName = "Wizyty"
        val notificationManager =
            getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Powiadomienia o wizytach"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Tworzenie powiadomienia
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Wskaźnik ikony powiadomienia
            .setContentTitle("Wizyta zapisana")
            .setContentText("Wizyta u ${Lista_lekarzy.NaszLekarz} na ${PacWizyty.selectedVisitDate}.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Powiadomienie znika po kliknięciu
            .build()

        // Wyświetlanie powiadomienia
        notificationManager.notify(1, notification) // ID powiadomienia może być dowolne
    }

    private fun isAppointmentCancelable(appointmentDate: String?): Boolean {
        if (appointmentDate == null) return false

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Format daty, dostosuj do formatu w jakim masz datę
        val appointmentDateTime = dateFormat.parse(appointmentDate)

        val currentDate = Date() // Aktualna data i godzina
        val timeDifference = appointmentDateTime.time - currentDate.time

        // Sprawdzamy, czy różnica jest większa niż 24 godziny (86400000 ms)
        return timeDifference > 0
    }
}