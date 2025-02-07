package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PotrzebneLogowanie : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_potrzebne_logowanie)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val Wybrana_data_wizyty = PacWizyty.selectedVisitDate
        val Wybrany_lekarz = Lista_lekarzy.NaszLekarz
        val textView = findViewById<TextView>(R.id.PotrzebneLogowanie_wybranawizyta)
        textView.text = "Wybrana wizyta: \n Data: $Wybrana_data_wizyty \n Lekarz: $Wybrany_lekarz"
        val Przycisk = findViewById<Button>(R.id.PotrzebneLogowanie_Przycisk_ZalogujSie)
        Przycisk.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}