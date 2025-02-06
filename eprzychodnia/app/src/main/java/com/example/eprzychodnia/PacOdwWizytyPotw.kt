package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class PacOdwWizytyPotw : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pac_odw_wizyty_potw)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val patientId = intent.getIntExtra("patient_id", -1).takeIf { it != -1 } // Domyślnie -1, jeśli brak
        val doctorId = intent.getIntExtra("doctor_id", -1).takeIf { it != -1 }
        val appointmentDate = intent.getStringExtra("appointment_date")
        val PrzyciskNIE = findViewById<Button>(R.id.PacOdwWizytyPotw_PrzyciskNie)

        PrzyciskNIE.setOnClickListener {
            finish()
        }

        val PrzyciskTAK = findViewById<Button>(R.id.PacOdwWizytyPotw_PrzyciskTak)
        PrzyciskTAK.setOnClickListener {
            updateAppointmentToNull(appointmentDate, doctorId)
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }
    }

    private fun updateAppointmentToNull(appointmentDate: String?, doctorId: Int?) {
        // Używamy odpowiedniego URL
        val url = getString(R.string.db_url_xampp) + "Odwolanie_wizyty_przez_pacjenta.php"

        // Przygotowanie zapytania do API, aby zaktualizować patient_id na NULL
        val requestQueue = Volley.newRequestQueue(this)
        val params = HashMap<String, String>()
        params["date"] = appointmentDate ?: ""
        params["doctor_id"] = doctorId?.toString() ?: ""
        params.remove("patient_id")

        // Używamy JSONObject do przesyłania danych
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, JSONObject(params as Map<*, *>?), // Jawne rzutowanie params na Map<*, *>?
            Response.Listener { response ->
                try {
                    Log.d("Request URL", url)
                    Log.d("Response", response.toString())
                    Log.d("Request Parameters", "doctor_id: $doctorId, appointment_date: $appointmentDate, patient_id: NULL")


                    if (response.getBoolean("success")) {
                        Toast.makeText(this, "Wizyta została anulowana", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Błąd: ${response.getString("message")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                if (error.networkResponse != null) {
                    val responseData = String(error.networkResponse.data, Charsets.UTF_8)
                    Log.e("PacjentWizyty", "Błąd: ${error.message}, Odpowiedź: $responseData")
                    Toast.makeText(this, "Błąd: $responseData", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("PacjentWizyty", "Błąd: ${error.message}")
                    Toast.makeText(this, "Błąd pobierania wizyt", Toast.LENGTH_SHORT).show()
                }
            }
        ) {}

        // Dodaj zapytanie do kolejki
        requestQueue.add(jsonObjectRequest)
    }

}
