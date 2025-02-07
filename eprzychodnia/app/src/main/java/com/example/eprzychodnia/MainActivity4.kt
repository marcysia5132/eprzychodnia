package com.example.eprzychodnia
import android.app.TaskStackBuilder
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        val user = MainActivity.username
        val userId = MainActivity.userId
        val textView: TextView = findViewById(R.id.textView)
        textView.text = "Witamy $user"

        val  PrzyciskListaLekarzy = findViewById<Button>(R.id.Lista)
        PrzyciskListaLekarzy.setOnClickListener{
            val  intent = Intent(this@MainActivity4, Lista_lekarzy::class.java)
            startActivity(intent)
        }
        val  PrzyciskHistoriaWizyt = findViewById<Button>(R.id.Hist)
        PrzyciskHistoriaWizyt.setOnClickListener{
            val  intent = Intent(this@MainActivity4, Historia_wizyt::class.java)
            startActivity(intent)
        }
        val przyciskWyloguj = findViewById<Button>(R.id.wyloguj)
        przyciskWyloguj.setOnClickListener {
            MainActivity.userId = -1
            val intentLogowanie = Intent(this@MainActivity4, MainActivity0::class.java)

            // Czyszczenie stosu i ustawienie nowej aktywności jako głównej
            intentLogowanie.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intentLogowanie)
            finish() // Kończymy bieżącą aktywność
        }
    }
}