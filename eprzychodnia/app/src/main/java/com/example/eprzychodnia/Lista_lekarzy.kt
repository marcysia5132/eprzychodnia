package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray

class Lista_lekarzy : AppCompatActivity() {
    companion object{
        var selectedDoctorId: Int = -1
        var NaszLekarz: String = ""
    }
    private lateinit var listView: ListView
    private val doctorList = mutableListOf<String>()
    private val doctorId = mutableListOf<Int>()
    private val doctorDetails = mutableListOf<Map<String, String>>()
    private fun fetchDoctors() {
        // Pobierz adres URL z pliku strings.xml
        val url = getString(R.string.doctors_url_xampp)

        // Utwórz zapytanie, które oczekuje odpowiedzi w postaci tablicy JSON
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response: JSONArray ->
                doctorList.clear()
                doctorId.clear()
                doctorDetails.clear()
                // Iterujemy po elementach tablicy JSON
                for (i in 0 until response.length()) {
                    val doctor = response.getJSONObject(i)
                    val firstName = doctor.getString("first_name")
                    val lastName = doctor.getString("last_name")
                    val specialty = doctor.getString("specialty")
                    val id_doctor = doctor.getInt("id_doctor")
                    // Łączymy imię, nazwisko i specjalizację w jeden ciąg
                    doctorList.add("$firstName $lastName - $specialty")
                    doctorId.add(id_doctor)
                    doctorDetails.add(mapOf(
                        "first_name" to firstName,
                        "last_name" to lastName,
                        "specialty" to specialty
                    ))
                }
                // Ustawiamy adapter, aby wyświetlić dane w ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, doctorList)
                listView.adapter = adapter
            },
            Response.ErrorListener { error ->
                Log.e("Lista lekarzy", "Błąd przy pobieraniu danych: $error")
            }
        )
        // Dodajemy zapytanie do kolejki Volley (korzystamy z singletona)
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_lekarzy)
        val user = MainActivity.username
        val userId = MainActivity.userId
        // Inicjalizacja ListView
        listView = findViewById(R.id.wyswietlana_lista)

        // Wywołanie funkcji pobierającej listę lekarzy
        fetchDoctors()

        listView.setOnItemClickListener { parent, view, position, id ->
            selectedDoctorId = doctorId[position]
            val selectedDoctor = doctorDetails[position]
            val firstName = selectedDoctor["first_name"]
            val lastName = selectedDoctor["last_name"]
            val specialty = selectedDoctor["specialty"]
            NaszLekarz = "$firstName $lastName - $specialty"
            val intent = Intent(this, PracWybrLek::class.java)
            startActivity(intent)
        }
    }
}