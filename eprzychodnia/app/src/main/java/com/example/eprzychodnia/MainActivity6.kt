package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray

class MainActivity6 : AppCompatActivity() {
    companion object{
        var selectedDoctorId: Int = -1
        var NaszLekarz: String = ""
    }
    private val PREFS_NAME = "AppPrefs"
    private val PREFS_THEME = "ThemeMode"
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
        setContentView(R.layout.activity_main6)

        val user = MainActivity.username
        val textView: TextView = findViewById(R.id.textView)
        textView.text = "Witamy $user"
        // Inicjalizacja ListView
        listView = findViewById(R.id.listView)

        // Wywołanie funkcji pobierającej listę lekarzy
        fetchDoctors()

        listView.setOnItemClickListener { parent, view, position, id ->
            selectedDoctorId = doctorId[position]
            val selectedDoctor = doctorDetails[position]
            val firstName = selectedDoctor["first_name"]
            val lastName = selectedDoctor["last_name"]
            val specialty = selectedDoctor["specialty"]
            NaszLekarz = "$firstName $lastName - $specialty"
            val intent = Intent(this, grafik::class.java)
            startActivity(intent)
        }

        val przyciskWyloguj = findViewById<Button>(R.id.wylogowanie)
        przyciskWyloguj.setOnClickListener {
            // Zmieniamy tryb na dzienny przed wylogowaniem
            setDayMode()

            MainActivity.userId = -1
            val intentLogowanie = Intent(this@MainActivity6, MainActivity0::class.java)

            // Czyszczenie stosu i ustawienie nowej aktywności jako głównej
            intentLogowanie.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intentLogowanie)
            finish() // Kończymy bieżącą aktywność
        }

        val preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val themeMode = preferences.getInt(PREFS_THEME, AppCompatDelegate.MODE_NIGHT_NO) // Domyślnie tryb dzienny
        AppCompatDelegate.setDefaultNightMode(themeMode)

        // Przycisk do przełączania motywu
        val switchThemeButton = findViewById<Button>(R.id.switchThemeButton)
        switchThemeButton.setOnClickListener {
            toggleTheme()
        }
    }

    private fun toggleTheme() {
        val preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = preferences.edit()

        // Zmieniamy tryb na odwrotny
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val newMode = if (currentMode == AppCompatDelegate.MODE_NIGHT_NO) {
            AppCompatDelegate.MODE_NIGHT_YES // Tryb nocny
        } else {
            AppCompatDelegate.MODE_NIGHT_NO // Tryb dzienny
        }

        AppCompatDelegate.setDefaultNightMode(newMode)
        editor.putInt(PREFS_THEME, newMode)
        editor.apply()
    }

    private fun setDayMode() {
        // Zmieniamy tryb na dzienny (app starts with day mode)
        val preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = preferences.edit()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        editor.putInt(PREFS_THEME, AppCompatDelegate.MODE_NIGHT_NO)
        editor.apply()
    }
}