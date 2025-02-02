package com.example.eprzychodnia
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)
        val user = MainActivity.username
        val userId = MainActivity.userId
        val textView: TextView = findViewById(R.id.textView)
        textView.text = "Jesteś zalogowany jako lekarz $user o ID: $userId"
    }
}
