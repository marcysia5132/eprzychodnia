package com.example.eprzychodnia
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
        val  PrzyciskWyloguj = findViewById<Button>(R.id.wyloguj)
        PrzyciskWyloguj.setOnClickListener{
            val  intent = Intent(this@MainActivity4, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}