package com.example.eprzychodnia

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdmWizytySzczegoly : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adm_wizyty_szczegoly)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val PacjentId = AdmWizyty.patientId
        val PacjentUsername = AdmWizyty.patientUsername
        val DoktorName = MainActivity6.NaszLekarz
        val DataWizyty = AdmWizyty.AppointmentDate
        val PacjentTextView = findViewById<TextView>(R.id.AdmWizytySzczegoly_Pacjent)
        if (PacjentId == -1) {
            PacjentTextView.text = "Wolny termin"
        } else {
            PacjentTextView.text = "Pacjent: $PacjentUsername"
        }
        val LekarzTextView = findViewById<TextView>(R.id.AdmWizytySzczegoly_Lekarz)
        LekarzTextView.text = "Lekarz: $DoktorName"
        val DataTextView = findViewById<TextView>(R.id.AdmWizytySzczegoly_Data)
        DataTextView.text = "Termin wizyty: $DataWizyty"
    }

}