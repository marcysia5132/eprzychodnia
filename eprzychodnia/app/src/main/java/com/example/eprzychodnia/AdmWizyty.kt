package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
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
import com.android.volley.toolbox.JsonObjectRequest
import com.example.eprzychodnia.PacWizyty.Companion.selectedVisitDate
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdmWizyty : AppCompatActivity() {
    companion object {
        var selectedVisitDate = ""
        var patientId = -1
        var patientUsername = ""
        var AppointmentDate = ""
        var AppointmentInfo = ""
    }
    val id_doctor = MainActivity6.selectedDoctorId
    private lateinit var listView: ListView
    val listawizyt = mutableListOf<String>()
    val listaIdPacjenta = mutableListOf<Int>()
    val listaUsernamePacjenta = mutableListOf<String>()
    val listaDatyWizyt = mutableListOf<String>()
    val listaInfoWizyt = mutableListOf<String>()
    private fun fetchUsername(id_pacjenta: Int, callback: (String) -> Unit) {
        val url = getString(R.string.get_username_url_xampp) + "?id=$id_pacjenta"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    if (response.getBoolean("success")) {
                        val username = response.getString("username")
                        callback(username)
                    } else {
                        callback("Nieznany")
                    }
                } catch (e: JSONException) {
                    Log.e("Fetch Username", "Błąd przy parsowaniu JSON: ${e.message}")
                    callback("Nieznany")
                }
            },
            Response.ErrorListener { error ->
                Log.e("Fetch Username", "Błąd przy pobieraniu username: $error")
                callback("Nieznany")
            }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun fetchWizyty() {
        val url = getString(R.string.get_appointments_url_xampp) + "?doctor_id=$id_doctor&selected_date=${PracWybrLek.selectedDateForDb}"
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response: JSONArray ->
                listawizyt.clear()
                listaIdPacjenta.clear()
                listaDatyWizyt.clear()
                listaUsernamePacjenta.clear()
                listaInfoWizyt.clear()
                val pendingRequests = mutableListOf<Boolean>()

                for (i in 0 until response.length()) {
                    val wizyta = response.getJSONObject(i)
                    val date = wizyta.getString("date")
                    val opiswizyty = wizyta.getString("info")
                    if (wizyta.isNull("patient_id")) {
                        listawizyt.add("$date   Wolny termin")
                        listaIdPacjenta.add(-1)
                        listaDatyWizyt.add("$date")
                        listaUsernamePacjenta.add("Nieznany")
                        listaInfoWizyt.add("Brak szczegółów wizyty")
                    } else {
                        val id_pacjenta = wizyta.getInt("patient_id")
                        pendingRequests.add(true) // Zaznaczamy, że oczekujemy na odpowiedź
                        fetchUsername(id_pacjenta) { username ->
                            listawizyt.add("$date   Pacjent: $username")
                            listaIdPacjenta.add(id_pacjenta)
                            listaUsernamePacjenta.add(username)
                            listaDatyWizyt.add("$date")
                            listaInfoWizyt.add("$opiswizyty")
                            pendingRequests.removeAt(0) // Usuwamy oczekujące zapytanie

                            // Jeśli to było ostatnie zapytanie, ustaw adapter
                            if (pendingRequests.isEmpty()) {
                                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listawizyt)
                                listView.adapter = adapter
                            }
                        }
                    }
                }

                // Jeśli nie było zapytań o nazwiska, ustaw adapter od razu
                if (pendingRequests.isEmpty()) {
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listawizyt)
                    listView.adapter = adapter
                }
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
        setContentView(R.layout.activity_adm_wizyty)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        listView = findViewById(R.id.AdmListaWizytyDlaDaty)
        val data_wizyt = findViewById<EditText>(R.id.AdmDataWizyt)
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
        val TextView = findViewById<TextView>(R.id.AdmWizyty_lekarz)
        TextView.text = MainActivity6.NaszLekarz
        fetchWizyty()

        val buttonAddAppointment: Button = findViewById(R.id.button2)
        buttonAddAppointment.visibility = Button.GONE
        if(isAppointmentCancelable(data_z_kalendarza)){
            buttonAddAppointment.visibility = Button.VISIBLE
        }
        buttonAddAppointment.setOnClickListener {
            val intent = Intent(this, AddAppointmentActivity::class.java).also {
                it.putExtra("doctorId", id_doctor)
                it.putExtra("selectedDate", data_z_kalendarza) // Załóżmy, że masz zmienną selectedDate
                startActivity(it)
            }
        }
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedVisit = listawizyt[position]
            val selectedPatientId = listaIdPacjenta[position]
            val selectedPatientUsername = listaUsernamePacjenta[position]
            val selectedAppointmentDate = listaDatyWizyt[position]
            val selectedAppointmentInfo = listaInfoWizyt[position]
            selectedVisitDate = selectedVisit
            patientId = selectedPatientId
            patientUsername = selectedPatientUsername
            AppointmentDate = selectedAppointmentDate
            AppointmentInfo = selectedAppointmentInfo
            val intentSzczegoly = Intent(this, AdmWizytySzczegoly::class.java)
            startActivity(intentSzczegoly)
        }
    }

    private fun isAppointmentCancelable(data_z_kalendarza: String?): Boolean {
        if (data_z_kalendarza == null) return false

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Tylko rok, miesiąc, dzień
        val appointmentDateOnly = dateFormat.parse(data_z_kalendarza) // Zamiana stringa na Date

        val currentDateOnly = dateFormat.parse(dateFormat.format(Date())) // Pobranie aktualnej daty BEZ godziny

        return !appointmentDateOnly.before(currentDateOnly) // Sprawdzenie, czy termin nie jest wcześniejszy niż dzisiejszy dzień
    }
}