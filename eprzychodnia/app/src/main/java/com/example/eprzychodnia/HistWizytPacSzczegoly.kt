package com.example.eprzychodnia

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
        val idPacjenta_textView = findViewById<TextView>(R.id.HistWizytPacSzczegoly_IdPacjenta)
        idPacjenta_textView.text = "Twoje id: $patientId"
        val SzczegolyWizyty_textView = findViewById<TextView>(R.id.HistWizytPacSzczegoly_SzczegolyWizyty)
        SzczegolyWizyty_textView.text = "Wizyta u lekarza: $doctorFirstName $doctorLastName - $doctorSpecialty, \n Data: $appointmentDate"
    }
}