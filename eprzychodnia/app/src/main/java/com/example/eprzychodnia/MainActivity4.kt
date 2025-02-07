package com.example.eprzychodnia

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity4 : AppCompatActivity() {

    private val PREFS_NAME = "AppPrefs"
    private val PREFS_THEME = "ThemeMode"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        val user = MainActivity.username
        val userId = MainActivity.userId
        val textView: TextView = findViewById(R.id.textView)
        textView.text = "Witamy $user"

        val przyciskListaLekarzy = findViewById<Button>(R.id.Lista)
        przyciskListaLekarzy.setOnClickListener {
            val intent = Intent(this@MainActivity4, Lista_lekarzy::class.java)
            startActivity(intent)
        }

        val przyciskHistoriaWizyt = findViewById<Button>(R.id.Hist)
        przyciskHistoriaWizyt.setOnClickListener {
            val intent = Intent(this@MainActivity4, Historia_wizyt::class.java)
            startActivity(intent)
        }

        val przyciskWyloguj = findViewById<Button>(R.id.wyloguj)
        przyciskWyloguj.setOnClickListener {
            // Ustawiamy tryb dzienny przed wylogowaniem
            setDayMode()

            MainActivity.userId = -1
            val intentLogowanie = Intent(this@MainActivity4, MainActivity0::class.java)

            // Czyszczenie stosu i ustawienie nowej aktywności jako głównej
            intentLogowanie.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentLogowanie)
            finish() // Kończymy bieżącą aktywność
        }

        // Zmieniamy motyw na podstawie zapisanej preferencji
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