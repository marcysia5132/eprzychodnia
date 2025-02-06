package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eprzychodnia.PracWybrLek.Companion
import java.util.Calendar

class grafik : AppCompatActivity() {
    companion object{
        var selectedDateForDb: String = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_grafik)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val lekarz = MainActivity6.NaszLekarz
        val textView = findViewById<TextView>(R.id.textView9)
        textView.text = "Grafik: $lekarz"

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val today = Calendar.getInstance()

        calendarView.date = today.timeInMillis

        calendarView.setOnDateChangeListener {view, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d",year,month + 1, dayOfMonth)

            Toast.makeText(this,"Wybrano datę: $selectedDate", Toast.LENGTH_SHORT).show()

            PracWybrLek.selectedDateForDb = "$selectedDate"

            val intent = Intent(this, AdmWizyty::class.java)
            startActivity(intent)
        }
    }
}