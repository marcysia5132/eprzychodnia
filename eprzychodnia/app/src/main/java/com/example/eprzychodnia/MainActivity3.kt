package com.example.eprzychodnia
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        val user = MainActivity.username
        val textView: TextView = findViewById(R.id.textView)
        textView.text = "Jesteś zalogowany jako $user"
    }

    fun onClickQuery(v: View) {
        val  user = MainActivity.username
        val  password = MainActivity.password
        val  query = findViewById<EditText>(R.id.editTextQuery).text.toString()
        val url = getString(R.string.db_url_xampp)

        // val url = getString(R.string.db_url)


        // Post parameters
        val jsonObject = JSONObject()
        jsonObject.put("username",user)
        jsonObject.put("password",password)
        jsonObject.put("email","")
        jsonObject.put("query",query)

        Log.d("fun onClickQuery:","jsonObject: $jsonObject")

        // Volley post request with parameters
        val requestPOST = JsonObjectRequest(Request.Method.POST,url,jsonObject,
            Response.Listener { response ->
                // Process the json
                try {

                    Log.d("fun onClickQuery:","Response: $response")
                    printResult(response)
                }catch (e:Exception){
                    Log.d("fun onClickQuery:","Exception: $e")
                }

            }, Response.ErrorListener{
                // Error in request
                Log.d("fun onClickQuery:","Volley error: $it")
            })

        VolleySingleton.getInstance(this).addToRequestQueue(requestPOST)

    }

    fun printResult(result : JSONObject) {
        findViewById<TextView>(R.id.resultTextView).text = result["message"].toString()
    }

}