package com.example.eprzychodnia
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    companion object {
        var username = ""
        var password = ""
        var session = ""
        var loggedin = false
        var rolaId = 0
        var userId: Int = -1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickSwitchToRegister(v: View) {
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
    }

    fun onClickLogin(v: View) {
        val  user = findViewById<EditText>(R.id.loginUserEditText).text.toString()
        val  password = findViewById<EditText>(R.id.loginPasswordEditText).text.toString()
        val url = getString(R.string.db_url_xampp)
        Log.d("fun onClickLogin:","url: $url")

        val jsonObject = JSONObject()
        jsonObject.put("username",user)
        jsonObject.put("password",password)
        jsonObject.put("email","")
        jsonObject.put("query","")

        val requestPOST = JsonObjectRequest(Request.Method.POST,url,jsonObject,
            Response.Listener { response ->
                try {
                    processResponse(response)
                    Log.d("fun onClickLogin:","Response: $response")
                }catch (e:Exception){
                    Log.d("fun onClickLogin:","Exception: $e")
                }

            }, Response.ErrorListener{ // Error in request
                Log.d("fun onClickLogin:","Volley error: $it")
            })

        MainActivity.username = user
        MainActivity.password = password
        VolleySingleton.getInstance(this).addToRequestQueue(requestPOST)
    }
    fun processResponse(response: JSONObject) {
        if (response.optInt("success", 0) == 1) {
            Toast.makeText(this, response.optString("message"), Toast.LENGTH_LONG).show()
            MainActivity.loggedin = true
            MainActivity.session = response.optString("session")

            val rolaId = response.optInt("rola_id", -1)
            val userId = response.optInt("id", -1)

            if (rolaId == -1 || userId == -1) {
                return
            }

            MainActivity.userId = userId
            val Pomoc = MainActivity0.Pomoc
            if (Pomoc == 1 && rolaId == 1) {
                val intent = Intent(this, PacPotwWizyty::class.java)
                startActivity(intent)
            } else {
                val nextActivity = when (rolaId) {
                    2 -> MainActivity5::class.java
                    3 -> MainActivity6::class.java
                    else -> MainActivity4::class.java
                }
                val intent = Intent(this, nextActivity)
                startActivity(intent)
                finish()
            }
        } else {
            MainActivity.loggedin = false
            MainActivity.session = ""
            MainActivity.username = ""
            MainActivity.password = ""

            Toast.makeText(this, response.optString("message"), Toast.LENGTH_LONG).show()
        }
    }



}