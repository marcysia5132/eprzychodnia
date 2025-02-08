package com.example.eprzychodnia

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AddAppointmentActivity : AppCompatActivity() {

    private lateinit var editTime: EditText
    private lateinit var buttonSave: Button
    private var selectedHour: Int = -1
    private var selectedMinute: Int = -1
    private var doctorId: Int = -1
    private lateinit var selectedDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_appointment)

        editTime = findViewById(R.id.edit_time)
        buttonSave = findViewById(R.id.button_save)

        doctorId = intent.getIntExtra("doctorId", -1)
        selectedDate = intent.getStringExtra("selectedDate") ?: ""

        editTime.setOnClickListener {
            showTimePicker()
        }

        buttonSave.setOnClickListener {
            if (selectedHour != -1 && selectedMinute != -1) {
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)

                if (isAppointmentCancelable(selectedDate, time)) {
                    addAppointmentToDatabase(selectedDate, time, doctorId)
                    val intent_1 = Intent(this, MainActivity6::class.java)
                    startActivity(intent_1)
                } else {
                    Toast.makeText(this, "Nie można dodać wizyty w przeszłości!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Wybierz godzinę!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        var hour = calendar.get(Calendar.HOUR_OF_DAY) + 1
        val minute = calendar.get(Calendar.MINUTE)
        if (hour >= 24) {hour = hour - 24}
        val timePicker = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                this.selectedHour = selectedHour
                this.selectedMinute = selectedMinute
                editTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            }, hour, minute, true
        )
        timePicker.show()
    }

    private fun addAppointmentToDatabase(date: String, time: String, doctorId: Int) {
        val url = getString(R.string.add_appointment_url_xampp)

        Log.d("AddAppointment", "Wysyłanie: date=$date, time=$time, doctor_id=$doctorId")

        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("AddAppointment", "Odpowiedź serwera: $response") // 🔍 Sprawdź, co zwraca PHP
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("success")) {
                        Toast.makeText(this, "Wizyta dodana!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Błąd dodawania wizyty!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("AddAppointment", "Błąd parsowania JSON: ${e.message}")
                    Toast.makeText(this, "Niepoprawna odpowiedź serwera", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("AddAppointment", "Błąd sieci: $error")
                Toast.makeText(this, "Błąd sieci: $error", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "date" to date,
                    "time" to time,
                    "doctor_id" to doctorId.toString()
                )
            }
        }

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun isAppointmentCancelable(date: String, time: String): Boolean {
        val dateTimeString = "$date $time" // Połączenie daty i godziny
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) // Format bez sekund

        return try {
            val appointmentDateTime = dateFormat.parse(dateTimeString)
            val currentDateTime = Date() // Aktualna data i godzina

            appointmentDateTime.after(currentDateTime) // Sprawdza, czy termin jest w przyszłości
        } catch (e: Exception) {
            Log.e("isAppointmentCancelable", "Błąd parsowania daty: ${e.message}")
            false
        }
    }

}
