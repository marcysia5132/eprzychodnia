package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PacWizyty : AppCompatActivity() {
    companion object {
        var selectedVisitDate = ""
    }
    val id_doctor = Lista_lekarzy.selectedDoctorId
    private lateinit var listView: ListView
    val listawizyt = mutableListOf<String>()
    var Sztuczne: Int = 0
    val Pomoc = MainActivity0.Pomoc
    val userId = MainActivity.userId
    private fun fetchWizyty() {
        val url = getString(R.string.get_appointments_url_xampp) + "?doctor_id=$id_doctor&selected_date=${PracWybrLek.selectedDateForDb}"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response: JSONArray ->
                listawizyt.clear()
                Sztuczne = 0

                for (i in 0 until response.length()) {
                    val wizyta = response.getJSONObject(i)
                    if (wizyta.isNull("patient_id")) {
                        val date = wizyta.getString("date")

                        // Sprawdzamy, czy termin jest jeszcze aktualny
                        if (isAppointmentCancelable(date)) {
                            listawizyt.add(date)
                            Sztuczne += 1
                        }
                    }
                }

                if (Sztuczne == 0) {
                    Toast.makeText(this, "Brak wolnych terminów w wybrany dzień", Toast.LENGTH_SHORT).show()
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listawizyt)
                listView.adapter = adapter
            },
            Response.ErrorListener { error ->
                Log.e("Wizyty pacjenta", "Błąd przy pobieraniu danych: $error")
            }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pac_wizyty)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        listView = findViewById(R.id.PacListaWizytDlaDaty)
        val data_wizyt = findViewById<EditText>(R.id.PacDataWizyt)
        val data_z_kalendarza = PracWybrLek.selectedDateForDb
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val date: Date = format.parse(data_z_kalendarza) ?: throw Exception("Nie udało się przekonwertować daty")
            val displayFormat = SimpleDateFormat("dd-MM-yyyy",Locale.getDefault())
            val formattedDate = displayFormat.format(date)
            data_wizyt.setText(formattedDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val TextView = findViewById<TextView>(R.id.PacWizyty_lekarz)
        TextView.text = Lista_lekarzy.NaszLekarz
        fetchWizyty()
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedVisit = listawizyt[position]
            selectedVisitDate = selectedVisit
            if(Pomoc == 1 && userId == -1) {
                val intent = Intent(this,PotrzebneLogowanie::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, PacPotwWizyty::class.java)
                startActivity(intent)
            }
        }
    }

    private fun isAppointmentCancelable(appointmentDate: String?): Boolean {
        if (appointmentDate == null) return false

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return try {
            val appointmentDateTime = dateFormat.parse(appointmentDate) ?: return false
            val currentDate = Date()
            val timeDifference = appointmentDateTime.time - currentDate.time

            // Sprawdzamy, czy różnica jest większa niż 24 godziny (86400000 ms)
            timeDifference > 0
        } catch (e: Exception) {
            Log.e("isAppointmentCancelable", "Błąd parsowania daty: ${e.message}")
            false
        }
    }

}