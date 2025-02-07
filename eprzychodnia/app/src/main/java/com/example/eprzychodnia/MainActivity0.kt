package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity0 : AppCompatActivity() {
    companion object {
        var Pomoc = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main0)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Pomoc = 0
        val buttonStartMain = findViewById<Button>(R.id.button_start_main)
        buttonStartMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val PrzyciskPrzegladajWizyty = findViewById<Button>(R.id.Main0_Przycisk_przegladaj_wizyty)
        PrzyciskPrzegladajWizyty.setOnClickListener {
            val intent = Intent(this, Lista_lekarzy::class.java)
            Pomoc = 1
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Pomoc = 0 // Resetowanie wartości po powrocie do tej aktywności
    }
}