package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class PracWybrLek : AppCompatActivity() {
        companion object{
        var selectedDateForDb: String = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prac_wybr_lek)
        val textView: TextView = findViewById(R.id.PracWybrLek_testid)
        val id_doctor = Lista_lekarzy.selectedDoctorId
        textView.text = "Id twojego lekarza: $id_doctor"
        val calendarView = findViewById<CalendarView>(R.id.PracWybrLek_kalendarz)
        val today = Calendar.getInstance()

        calendarView.date = today.timeInMillis

        calendarView.setOnDateChangeListener {view, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d",year,month + 1, dayOfMonth)

            Toast.makeText(this,"Wybrano datę: $selectedDate", Toast.LENGTH_SHORT).show()

            selectedDateForDb = "$selectedDate"

            val intent = Intent(this, PacWizyty::class.java)
            startActivity(intent)
        }

    }
}