package com.example.eprzychodnia
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        val user = MainActivity.username
        val userId = MainActivity.userId
        val textView: TextView = findViewById(R.id.textView)
        textView.text = "Witamy $user"
    }
}