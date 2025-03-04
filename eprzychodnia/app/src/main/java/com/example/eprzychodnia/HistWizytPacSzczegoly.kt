package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*

class HistWizytPacSzczegoly : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hist_wizyt_pac_szczegoly)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val user = MainActivity.username
        val textView = findViewById<TextView>(R.id.HistWizytPacSzczegoly_nazwaPacjenta)
        textView.text = "$user oto szczegóły wybranej wizyty:"
        val patientId: Int? = intent.getIntExtra("patient_id", -1).takeIf { it != -1 } // Domyślnie -1, jeśli brak
        val appointmentDate = intent.getStringExtra("appointment_date")
        val doctorFirstName = intent.getStringExtra("doctor_first_name")
        val doctorLastName = intent.getStringExtra("doctor_last_name")
        val doctorSpecialty = intent.getStringExtra("doctor_specialty")
        var VisitInfo = intent.getStringExtra("info")
        if (VisitInfo == "") {VisitInfo = "Brak szczegółów wizyty"}
        val doctorId = intent.getIntExtra("doctor_id", -1).takeIf { it != -1 } // Domyślnie -1, jeśli brak
        val SzczegolyWizyty_textView = findViewById<TextView>(R.id.HistWizytPacSzczegoly_SzczegolyWizyty)
        SzczegolyWizyty_textView.text = "Wizyta u lekarza: $doctorFirstName $doctorLastName - $doctorSpecialty, \n Data: $appointmentDate"
        val OpisWizyty_textView = findViewById<TextView>(R.id.HistWizytPacSzczegoly_OpisWizyty)
        OpisWizyty_textView.text = "Info: $VisitInfo"
        val ZnikajacyTekst = findViewById<TextView>(R.id.textView15)
        ZnikajacyTekst.visibility = TextView.GONE
        val cancelButton = findViewById<Button>(R.id.cancelAppointmentButton)
        cancelButton.visibility = Button.GONE // Ukryj przycisk na początku

        // Sprawdzenie, czy data wizyty jest oddalona o co najmniej 24 godziny od teraz
        if (isAppointmentCancelable(appointmentDate)) {
            cancelButton.visibility = Button.VISIBLE // Pokaż przycisk, jeśli spełnia warunki
            ZnikajacyTekst.visibility = TextView.VISIBLE
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, PacOdwWizytyPotw::class.java)
            intent.putExtra("appointment_date", appointmentDate)
            intent.putExtra("doctor_id", doctorId)
            intent.putExtra("patient_id", patientId)
            startActivity(intent)
        }
    }

    // Funkcja sprawdzająca, czy wizyta jest oddalona o co najmniej 24 godziny od teraz
    private fun isAppointmentCancelable(appointmentDate: String?): Boolean {
        if (appointmentDate == null) return false

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Format daty, dostosuj do formatu w jakim masz datę
        val appointmentDateTime = dateFormat.parse(appointmentDate)

        val currentDate = Date() // Aktualna data i godzina
        val timeDifference = appointmentDateTime.time - currentDate.time

        // Sprawdzamy, czy różnica jest większa niż 24 godziny (86400000 ms)
        return timeDifference > 86400000
    }
}
