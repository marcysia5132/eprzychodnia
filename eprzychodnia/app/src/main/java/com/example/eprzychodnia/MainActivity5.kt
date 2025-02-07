package com.example.eprzychodnia
import android.app.TaskStackBuilder
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
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
        textView.text = "Witamy $user"
        listView = findViewById(R.id.listView)
        fetchAppointments()
        val przyciskWyloguj = findViewById<Button>(R.id.wyloguj)
        przyciskWyloguj.setOnClickListener {
            MainActivity.userId = -1
            val intentLogowanie = Intent(this@MainActivity5, MainActivity::class.java)
            val sztucznaAktywnosc = TaskStackBuilder.create(this).apply {
                addNextIntent(Intent(this@MainActivity5, MainActivity0::class.java))
                addNextIntent(intentLogowanie)
            }
            sztucznaAktywnosc.startActivities()
            finish()
        }
    }

    private fun fetchAppointments() {
        // Pobierz adres URL z pliku strings.xml
        val url = getString(R.string.appointments_url_xampp)
        val userId = MainActivity.userId

        // Utwórz zapytanie, które oczekuje odpowiedzi w postaci tablicy JSON
        val request = JsonArrayRequest(
            Request.Method.GET,
            "${url}?user_id=${userId}",
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
                    val info = doctor.getString("info")
                    appointmentsList.add(date + " - " + patient + "\n" + info);
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