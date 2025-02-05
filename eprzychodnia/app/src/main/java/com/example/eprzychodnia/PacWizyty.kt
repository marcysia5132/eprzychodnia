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
    private fun fetchWizyty() {
        // Pobierz adres URL z pliku strings.xml
        val url = getString(R.string.get_appointments_url_xampp) + "?doctor_id=$id_doctor&selected_date=${PracWybrLek.selectedDateForDb}"

        // Utwórz zapytanie, które oczekuje odpowiedzi w postaci tablicy JSON
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response: JSONArray ->
                listawizyt.clear()
                // Iterujemy po elementach tablicy JSON
                if (response.length() == 0) {
                    // Jeśli serwer zwrócił pustą tablicę, wyświetl komunikat
                    Toast.makeText(this, "Brak wolnych terminów w wybrany dzień", Toast.LENGTH_SHORT).show()
                } else {
                    // Przetwarzamy terminy
                    for (i in 0 until response.length()) {
                        val wizyta = response.getJSONObject(i)
                        if (wizyta.isNull("patient_id")) {
                            val date = wizyta.getString("date")
                            listawizyt.add(date)
                        }
                    }
                }
                // Ustawiamy adapter, aby wyświetlić dane w ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listawizyt)
                listView.adapter = adapter
            },
            Response.ErrorListener { error ->
                Log.e("Wizyty pacjenta", "Błąd przy pobieraniu danych: $error")
            }
        )
        // Dodajemy zapytanie do kolejki Volley (korzystamy z singletona)
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

            val intent = Intent(this, PacPotwWizyty::class.java)
            startActivity(intent)
        }
    }
}