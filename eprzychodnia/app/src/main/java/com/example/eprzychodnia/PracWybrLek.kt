package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class PracWybrLek : AppCompatActivity() {
    companion object {
        var selectedDateForDb: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prac_wybr_lek)

        val textView: TextView = findViewById(R.id.PracWybrLek_NazwaLekarza)
        val doctor = Lista_lekarzy.NaszLekarz
        textView.text = "Wybrany lekarz:  $doctor"

        val calendarView = findViewById<CalendarView>(R.id.PracWybrLek_kalendarz)
        val today = Calendar.getInstance()
        calendarView.date = today.timeInMillis

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

            if (!isAppointmentCancelable(selectedDate)) {
                Toast.makeText(this, "Nie można wybrać daty z przeszłości!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Wybrano datę: $selectedDate", Toast.LENGTH_SHORT).show()
                selectedDateForDb = selectedDate

                val intent = Intent(this, PacWizyty::class.java)
                startActivity(intent)
            }
        }
    }

    private fun isAppointmentCancelable(dataZKalendarza: String?): Boolean {
        if (dataZKalendarza == null) return false

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Tylko rok, miesiąc, dzień
        val appointmentDateOnly = dateFormat.parse(dataZKalendarza) // Zamiana stringa na Date
        val currentDateOnly = dateFormat.parse(dateFormat.format(Date())) // Pobranie aktualnej daty BEZ godziny

        return !appointmentDateOnly.before(currentDateOnly) // Sprawdzenie, czy termin nie jest wcześniejszy niż dzisiejszy dzień
    }
}
