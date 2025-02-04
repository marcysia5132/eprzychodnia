package com.example.eprzychodnia

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray

class Lista_lekarzy : AppCompatActivity() {
    private lateinit var listView: ListView
    private val doctorList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)
        val user = MainActivity.username
        val userId = MainActivity.userId
        val textView: TextView = findViewById(R.id.textView)
        textView.text = "Witamy $user"
        // Inicjalizacja ListView
        listView = findViewById(R.id.listView)

        // Wywołanie funkcji pobierającej listę lekarzy
        fetchDoctors()
    }

    private fun fetchDoctors() {
        // Pobierz adres URL z pliku strings.xml
        val url = getString(R.string.doctors_url_xampp)

        // Utwórz zapytanie, które oczekuje odpowiedzi w postaci tablicy JSON
        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener { response: JSONArray ->
                doctorList.clear()
                // Iterujemy po elementach tablicy JSON
                for (i in 0 until response.length()) {
                    val doctor = response.getJSONObject(i)
                    val firstName = doctor.getString("first_name")
                    val lastName = doctor.getString("last_name")
                    val specialty = doctor.getString("specialty")
                    // Łączymy imię, nazwisko i specjalizację w jeden ciąg
                    doctorList.add("$firstName $lastName - $specialty")
                }
                // Ustawiamy adapter, aby wyświetlić dane w ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, doctorList)
                listView.adapter = adapter
            },
            Response.ErrorListener { error ->
                Log.e("MainActivity6", "Błąd przy pobieraniu danych: $error")
            }
        )
        // Dodajemy zapytanie do kolejki Volley (korzystamy z singletona)
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }
}
