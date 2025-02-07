package com.example.eprzychodnia

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.Spanned
import android.text.Html
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Ustawienie tekstu regulaminu z hiperłączem
        val termsText = findViewById<TextView>(R.id.termsText)
        val termsTextContent = "Akceptuję <u>regulamin</u> korzystania z aplikacji."
        termsText.text = Html.fromHtml(termsTextContent, Html.FROM_HTML_MODE_LEGACY)
        termsText.movementMethod = LinkMovementMethod.getInstance()

        // Obsługa kliknięcia w "regulamin"
        termsText.setOnClickListener {
            showTermsDialog()
        }
    }

    // Metoda do wyświetlania regulaminu w oknie dialogowym
    private fun showTermsDialog() {
        val terms = """
            **Regulamin Aplikacji e-Przychodnia**

            **1. Postanowienia ogólne**
            1.1. Niniejszy regulamin określa zasady korzystania z aplikacji mobilnej e-Przychodnia.
            1.2. Aplikacja e-Przychodnia umożliwia rejestrację, umawianie wizyt lekarskich oraz zarządzanie danymi pacjentów i lekarzy.
            1.3. Właścicielem aplikacji jest [Nazwa Firmy lub Podmiotu] z siedzibą w [Adres Firmy].
            1.4. Korzystanie z aplikacji oznacza akceptację niniejszego regulaminu.

            **2. Warunki korzystania**
            2.1. Użytkownikiem aplikacji może być każda osoba pełnoletnia lub posiadająca zgodę opiekuna prawnego.
            2.2. Rejestracja w aplikacji wymaga podania prawdziwych danych osobowych.
            2.3. Użytkownik zobowiązuje się do przestrzegania zasad bezpieczeństwa oraz nieudostępniania danych logowania osobom trzecim.

            **3. Rejestracja i konto użytkownika**
            3.1. Każdy użytkownik może posiadać jedno konto w aplikacji.
            3.2. Podczas rejestracji należy podać dane niezbędne do korzystania z usług aplikacji.
            3.3. Użytkownik może w dowolnym momencie usunąć swoje konto, kontaktując się z administratorem aplikacji.

            **4. Umawianie wizyt**
            4.1. Aplikacja umożliwia rezerwację wizyt u lekarzy dostępnych w systemie.
            4.2. Użytkownik zobowiązany jest do anulowania wizyty w przypadku niemożności jej odbycia.
            4.3. Lekarze mogą aktualizować szczegóły wizyt oraz wprowadzać informacje o pacjentach.

            **5. Ochrona danych osobowych**
            5.1. Administratorem danych osobowych jest [Nazwa Firmy].
            5.2. Dane osobowe są przetwarzane zgodnie z obowiązującymi przepisami prawa oraz polityką prywatności aplikacji.
            5.3. Każdy użytkownik ma prawo do wglądu, modyfikacji i usunięcia swoich danych.

            **6. Odpowiedzialność**
            6.1. Operator aplikacji nie ponosi odpowiedzialności za błędne dane wprowadzone przez użytkowników.
            6.2. Operator nie ponosi odpowiedzialności za brak dostępności aplikacji z przyczyn technicznych lub siły wyższej.
            6.3. Każde naruszenie regulaminu może skutkować zablokowaniem konta użytkownika.

            **7. Postanowienia końcowe**
            7.1. Regulamin może być zmieniany w dowolnym czasie, a zmiany będą ogłaszane w aplikacji.
            7.2. W sprawach nieuregulowanych niniejszym regulaminem zastosowanie mają przepisy prawa polskiego.
            7.3. Wszelkie spory wynikające z korzystania z aplikacji rozstrzygane będą przez sąd właściwy dla siedziby operatora aplikacji.


        """.trimIndent()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Regulamin")
        val scrollView = android.widget.ScrollView(this)
        val textView = android.widget.TextView(this)
        textView.text = terms
        textView.setPadding(50, 20, 50, 20)
        scrollView.addView(textView)
        builder.setView(scrollView)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    fun onClickSwitchToLogin(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun onClickRegister(v: View) {
        val termsChecked = findViewById<CheckBox>(R.id.termsCheckbox).isChecked
        if (!termsChecked) {
            Toast.makeText(this, "Musisz zaakceptować regulamin!", Toast.LENGTH_LONG).show()
            return
        }
        val user = findViewById<EditText>(R.id.registerUserEditText).text.toString()
        val email = findViewById<EditText>(R.id.registerEmailEditText).text.toString()
        val password = findViewById<EditText>(R.id.registerPasswordlEditText).text.toString()
        val password2 = findViewById<EditText>(R.id.registerConfirmPasswordlEditText).text.toString()

        if (password != password2) {
            Toast.makeText(this, "Hasła nie pasują!", Toast.LENGTH_LONG).show()
            return
        }

        val url = getString(R.string.db_url_xampp)
        val jsonObject = JSONObject()
        jsonObject.put("username", user)
        jsonObject.put("password", password)
        jsonObject.put("email", email)
        jsonObject.put("query", "")

        // Volley post request
        val requestPOST = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            Response.Listener { response ->
                try {
                    processResponse(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener {
                it.printStackTrace()
            })

        VolleySingleton.getInstance(this).addToRequestQueue(requestPOST)
    }

    fun processResponse(response: JSONObject) {
        if (response["success"] == 1) {
            Toast.makeText(this, response["message"].toString(), Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, response["message"].toString(), Toast.LENGTH_LONG).show()
        }
    }
}