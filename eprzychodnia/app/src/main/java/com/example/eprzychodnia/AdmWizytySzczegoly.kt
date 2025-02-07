package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
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
        val appointmentDate = AdmWizyty.AppointmentDate
        val doctorId = MainActivity6.selectedDoctorId
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

        val PrzyciskOdwolaj = findViewById<Button>(R.id.AdmWizytySzczegoly_Przycisk_Odwolaj)
        PrzyciskOdwolaj.visibility = Button.GONE
        val TekstOdwolaj = findViewById<TextView>(R.id.textView20)
        TekstOdwolaj.visibility = TextView.GONE

        if(PacjentId != -1){
            PrzyciskOdwolaj.visibility = Button.VISIBLE
            TekstOdwolaj.visibility = TextView.VISIBLE
        }
        PrzyciskOdwolaj.setOnClickListener {
            updateAppointmentToNull(appointmentDate, doctorId)
            val intent = Intent(this, MainActivity6::class.java)
            startActivity(intent)
        }
        val PrzyciskUsun = findViewById<Button>(R.id.AdmWizytySzczegoly_Przycisk_Usun)
        PrzyciskUsun.setOnClickListener {
            deleteAppointment(appointmentDate, doctorId)
            val intent = Intent(this, MainActivity6::class.java)
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

    private fun deleteAppointment(appointmentDate: String?, doctorId: Int?) {
        val url = getString(R.string.db_url_xampp) + "Usuwanie_wizyty_przez_administracje.php"

        val requestQueue = Volley.newRequestQueue(this)
        val params = HashMap<String, String>()
        params["date"] = appointmentDate ?: ""
        params["doctor_id"] = doctorId?.toString() ?: ""

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, JSONObject(params as Map<*, *>?),
            Response.Listener { response ->
                try {
                    Log.d("Request URL", url)
                    Log.d("Response", response.toString())
                    Log.d("Request Parameters", "doctor_id: $doctorId, appointment_date: $appointmentDate")

                    if (response.getBoolean("success")) {
                        Toast.makeText(this, "Wizyta została usunięta", Toast.LENGTH_SHORT).show()
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

        requestQueue.add(jsonObjectRequest)
    }

}