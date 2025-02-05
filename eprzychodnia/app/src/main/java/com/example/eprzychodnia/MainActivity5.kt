package com.example.eprzychodnia
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray

class MainActivity5 : AppCompatActivity() {
    private lateinit var listView: ListView
    private val appointmentsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)
        val user = MainActivity.username
        val userId = MainActivity.userId
        val textView: TextView = findViewById(R.id.textView)
        textView.text = "Jesteś zalogowany jako lekarz $user o ID: $userId"
        listView = findViewById(R.id.listView)
        fetchAppointments()
    }

    private fun fetchAppointments() {
        // Pobierz adres URL z pliku strings.xml
        val url = getString(R.string.appointments_url_xampp)
        val userId = MainActivity.userId

        // Utwórz zapytanie, które oczekuje odpowiedzi w postaci tablicy JSON
        val request = JsonArrayRequest(
            Request.Method.GET,
            "${url}?${userId}",
            null,
            Response.Listener { response: JSONArray ->
                appointmentsList.clear()
                // Iterujemy po elementach tablicy JSON
                for (i in 0 until response.length()) {
                    val doctor = response.getJSONObject(i)
                    val date = doctor.getString("date")
                    Log.d("elo", date)
                    Log.d("elo", doctor.toString())
                    val patient = doctor.getString("username")
                    appointmentsList.add("$date - $patient")
                }
                // Ustawiamy adapter, aby wyświetlić dane w ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, appointmentsList)
                listView.adapter = adapter
            },
            Response.ErrorListener { error ->
                Log.e("MainActivity5", "Błąd przy pobieraniu danych: $error")
            }
        )
        // Dodajemy zapytanie do kolejki Volley (korzystamy z singletona)
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }
}
