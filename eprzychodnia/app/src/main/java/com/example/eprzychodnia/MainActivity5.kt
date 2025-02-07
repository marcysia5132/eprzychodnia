package com.example.eprzychodnia

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import org.json.JSONArray


class MainActivity5 : AppCompatActivity() {

    private lateinit var listView: ListView
    private val appointmentsList = mutableListOf<String>()
    private val appointmentsIdList = mutableListOf<Int>()
    private val appointmentsInfoList = mutableListOf<String>()
    private var selectedAppointmentId: Int = -1
    private lateinit var adapter: ArrayAdapter<String>
    private val PREFS_NAME = "AppPrefs"
    private val PREFS_THEME = "ThemeMode"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        val user = MainActivity.username
        val textView: TextView = findViewById(R.id.textView)
        textView.text = "Witamy $user"
        listView = findViewById(R.id.listView)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, appointmentsList)
        listView.adapter = adapter

        fetchAppointments()

        val przyciskWyloguj = findViewById<Button>(R.id.wyloguj)
        przyciskWyloguj.setOnClickListener {
            // Zmieniamy tryb na dzienny przed wylogowaniem
            setDayMode()

            MainActivity.userId = -1
            finish()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedAppointmentId = appointmentsIdList[position]
            showAppointmentEditDialog(position)
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

    private fun fetchAppointments() {
        val url = getString(R.string.appointments_url_xampp)
        val userId = MainActivity.userId

        val request = JsonArrayRequest(
            Request.Method.GET,
            "$url?user_id=$userId",
            null,
            Response.Listener { response: JSONArray ->
                appointmentsList.clear()
                appointmentsIdList.clear()
                appointmentsInfoList.clear()

                for (i in 0 until response.length()) {
                    val appointment = response.getJSONObject(i)
                    val appointmentId = appointment.getInt("id")
                    val date = appointment.getString("date")
                    val patient = appointment.getString("username")
                    val info = appointment.optString("info", "Brak szczegółów")

                    val appointmentDetails = "$date - $patient\n$info"
                    appointmentsList.add(appointmentDetails)
                    appointmentsIdList.add(appointmentId)
                    appointmentsInfoList.add(info)
                }

                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Log.e("MainActivity5", "Błąd przy pobieraniu danych: $error")
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun showAppointmentEditDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edytuj szczegóły wizyty")

        val input = EditText(this)
        input.setText(appointmentsInfoList[position])
        builder.setView(input)

        builder.setPositiveButton("Zapisz") { dialog, _ ->
            val updatedDetails = input.text.toString()
            updateAppointmentInfoInDatabase(position, updatedDetails)
            dialog.dismiss()
        }

        builder.setNegativeButton("Anuluj") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun updateAppointmentInfoInDatabase(position: Int, updatedDetails: String) {
        val url = getString(R.string.update_appointment_info_url)

        val jsonObject = JSONObject()
        jsonObject.put("appointment_id", selectedAppointmentId)
        jsonObject.put("info", updatedDetails)

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonObject,
            Response.Listener { response ->
                Log.d("Update", "Odpowiedź serwera: $response")
                val status = response.optString("status")
                if (status == "success") {
                    Log.d("Update", "Szczegóły wizyty zaktualizowane w bazie.")

                    // **1. Aktualizujemy dane w kodzie**
                    appointmentsInfoList[position] = updatedDetails
                    appointmentsList[position] = appointmentsList[position].substringBefore("\n") + "\n" + updatedDetails

                    // **2. Odświeżamy adapter**
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("Update", "Błąd przy aktualizacji: ${response.optString("message")}")
                }
            },
            Response.ErrorListener { error ->
                Log.e("Update", "Błąd przy aktualizacji: $error")
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
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