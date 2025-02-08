package com.example.eprzychodnia

import android.app.AlertDialog
import android.content.Intent
import android.icu.text.IDNA.Info
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        var InfoWizyty = AdmWizyty.AppointmentInfo
        if (InfoWizyty == "") {InfoWizyty = "Brak szczegółów wizyty"}
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
        val AppointmentInfoTextView = findViewById<TextView>(R.id.AdmWizytySzczegoly_OpisWizyty)
        AppointmentInfoTextView.text = "Info: $InfoWizyty"
        val AdmWizytySzczegolyPrzyciskUsun = findViewById<Button>(R.id.AdmWizytySzczegoly_Przycisk_Usun)
        AdmWizytySzczegolyPrzyciskUsun.visibility = Button.GONE
        val AdmWizytySzczegolyTekstUsun = findViewById<TextView>(R.id.textView19)
        AdmWizytySzczegolyTekstUsun.visibility = TextView.GONE
        val PrzyciskOdwolaj = findViewById<Button>(R.id.AdmWizytySzczegoly_Przycisk_Odwolaj)
        PrzyciskOdwolaj.visibility = Button.GONE
        val TekstOdwolaj = findViewById<TextView>(R.id.textView20)
        TekstOdwolaj.visibility = TextView.GONE
        val PrzyciskZapisz = findViewById<Button>(R.id.AdmWizytySzczegoly_Przycisk_Zapisz)
        PrzyciskZapisz.visibility = Button.GONE
        val TekstZapisz = findViewById<TextView>(R.id.textView16)
        TekstZapisz.visibility = TextView.GONE
        if(isAppointmentCancelable(appointmentDate)) {
            if (PacjentId != -1) {
                PrzyciskOdwolaj.visibility = Button.VISIBLE
                TekstOdwolaj.visibility = TextView.VISIBLE
            }
            AdmWizytySzczegolyPrzyciskUsun.visibility = Button.VISIBLE
            AdmWizytySzczegolyTekstUsun.visibility = TextView.VISIBLE
            if (PacjentId == -1) {
                PrzyciskZapisz.visibility = Button.VISIBLE
                TekstZapisz.visibility = TextView.VISIBLE
            }
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
        PrzyciskZapisz.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Podaj ID pacjenta:")

            // Tworzenie pola do wpisania ID pacjenta
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            builder.setView(input)

            builder.setPositiveButton("Zapisz") { _, _ ->
                val inputText = input.text.toString()
                if (inputText.isNotEmpty()) {
                    val newPatientId = inputText.toInt()
                    saveAppointment(newPatientId) // Przekazujemy ID pacjenta
                } else {
                    Toast.makeText(this, "ID pacjenta nie może być puste", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("Anuluj") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
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
        params.remove("info")

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

    private fun isAppointmentCancelable(appointmentDate: String?): Boolean {
        if (appointmentDate == null) return false

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Format daty, dostosuj do formatu w jakim masz datę
        val appointmentDateTime = dateFormat.parse(appointmentDate)

        val currentDate = Date() // Aktualna data i godzina
        val timeDifference = appointmentDateTime.time - currentDate.time

        // Sprawdzamy, czy różnica jest większa niż 24 godziny (86400000 ms)
        return timeDifference > 0
    }

    private fun saveAppointment(newPatientId: Int) {
        val url = getString(R.string.db_url_xampp) + "save_appointment.php"

        val params = HashMap<String, String>()
        params["doctor_id"] = MainActivity6.selectedDoctorId.toString()
        params["patient_id"] = newPatientId.toString() // Używamy nowego ID pacjenta
        params["date"] = AdmWizyty.AppointmentDate

        Log.d("SaveAppointment", "Wysyłane dane: $params")

        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Log.d("SaveAppointment", "Odpowiedź z serwera: $response")
                Toast.makeText(this, "Wizyta zapisana!", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Log.e("SaveAppointment", "Błąd przy zapisie wizyty: $error")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return params
            }
        }

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }
}