package com.example.eprzychodnia

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.eprzychodnia.PacWizyty.Companion.selectedVisitDate
import org.json.JSONObject
import org.w3c.dom.Text

class PacPotwWizyty : AppCompatActivity() {
    private lateinit var confrimButton: Button
    val id_doctor = Lista_lekarzy.selectedDoctorId
    val id_pacjenta = MainActivity.userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pac_potw_wizyty)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val Wybrana_data_wizyty = PacWizyty.selectedVisitDate
        val Wybrany_lekarz = Lista_lekarzy.NaszLekarz

        val SzczegolywizytyTextView = findViewById<TextView>(R.id.PacPotwWizyty_szczegoly)
        SzczegolywizytyTextView.text = "Wizyta zaplanowana na: $Wybrana_data_wizyty"
        val WizytaLekarzTextView = findViewById<TextView>(R.id.PacPotwWizyty_lekarz)
        WizytaLekarzTextView.text = "Lekarz przyjmujący: $Wybrany_lekarz"

        confrimButton = findViewById(R.id.PacPotwWizte_przycisk)
        confrimButton.setOnClickListener {
            saveAppointment()
        }
    }
    private fun saveAppointment() {
        val url = getString(R.string.db_url_xampp) + "save_appointment.php"

        val params = HashMap<String, String>()
        params["doctor_id"] = id_doctor.toString()
        params["patient_id"] = id_pacjenta.toString()
        params["date"] = selectedVisitDate

        Log.d("SaveAppointment", "Wysyłane dane: $params") // <-- SPRAWDZAMY DANE

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