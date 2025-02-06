package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException

class Historia_wizyt : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val wizytyList = mutableListOf<String>()
    private val wizytyData = mutableListOf<Appointment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historia_wizyt)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val user = MainActivity.username
        val textView = findViewById<TextView>(R.id.textView10)
        textView.text = "Twoja historia wizyt $user:"
        listView = findViewById(R.id.HistWizyt_ListaWizyt)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, wizytyList)
        listView.adapter = adapter

        loadAppointments()

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedAppointment = wizytyData[position]
            val intent = Intent(this, HistWizytPacSzczegoly::class.java)
            intent.putExtra("patient_id", selectedAppointment.id)
            intent.putExtra("appointment_date", selectedAppointment.date)
            intent.putExtra("doctor_first_name", selectedAppointment.doctorFirstName)
            intent.putExtra("doctor_last_name", selectedAppointment.doctorLastName)
            intent.putExtra("doctor_specialty", selectedAppointment.doctorSpecialty)
            startActivity(intent)
        }
    }
    private fun loadAppointments() {
        val url = getString(R.string.get_patient_appointments_url_xampp) + "?patient_id=" + MainActivity.userId

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    if (response.getBoolean("success")) {
                        wizytyList.clear()
                        wizytyData.clear()

                        val appointments = response.getJSONArray("appointments")
                        for (i in 0 until appointments.length()) {
                            val obj = appointments.getJSONObject(i)
                            val appointment = Appointment(
                                obj.getString("date"),
                                obj.getString("first_name"),
                                obj.getString("last_name"),
                                obj.getString("specialty"),
                                obj.getInt("patient_id")
                            )
                            wizytyData.add(appointment)
                            wizytyList.add("Wizyta u lekarza: ${appointment.doctorFirstName} ${appointment.doctorLastName} - ${appointment.doctorSpecialty}, \n Data: ${appointment.date}")
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this, "Brak wizyt", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                if (error.networkResponse != null) {
                    val responseData = String(error.networkResponse.data, Charsets.UTF_8)
                    Log.e("PacjentWizyty", "Błąd: ${error.message}, Odpowiedź: $responseData")
                } else {
                    Log.e("PacjentWizyty", "Błąd: ${error.message}")
                }
                Toast.makeText(this, "Błąd pobierania wizyt", Toast.LENGTH_SHORT).show()
            })

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    data class Appointment(val date: String, val doctorFirstName: String, val doctorLastName: String, val doctorSpecialty: String, val id: Int)
}