package com.jatin.foodjunction.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.jatin.foodjunction.R
import com.jatin.foodjunction.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {

    private lateinit var etName : EditText
    private lateinit var etMobile : EditText
    private lateinit var etMail : EditText
    private lateinit var etAdd : EditText
    private lateinit var etPass : EditText
    private lateinit var etCPass : EditText
    private lateinit var btnSignUp : Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_scroll)

        supportActionBar?.hide()

        etName = findViewById(R.id.etNameSignUp)
        etMobile = findViewById(R.id.etMobileSignUp)
        etMail = findViewById(R.id.etMailSignUp)
        etAdd = findViewById(R.id.etAddSignUp)
        etPass = findViewById(R.id.etPassSignUp)
        etCPass = findViewById(R.id.etCPassSignUp)
        btnSignUp = findViewById(R.id.btnSignUp)

        sharedPreferences = getSharedPreferences("RegistrationPreferences",Context.MODE_PRIVATE)

        btnSignUp.setOnClickListener {

            userRegistration()

        }
    }

    private fun userRegistration(){

        sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()

        if(ConnectionManager().checkConnectivity(this)){

            if(noErrors()){

                try {

                    val user = JSONObject()

                    user.put("name", etName.text.toString())
                    user.put("mobile_number", etMobile.text.toString())
                    user.put("password", etPass.text.toString())
                    user.put("address", etAdd.text.toString())
                    user.put("email", etMail.text.toString())

                    val queue = Volley.newRequestQueue(this)
                    val url =  "http://192.168.43.212/foodrunner_api_scripts/v2/register/fetch_result.php"

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Method.POST,url,user,

                        Response.Listener {

                            val response = it.getJSONObject("data")

                            val success = response.getBoolean("success")

                            if (success){

                                val data = response.getJSONObject("data")

                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()

                                sharedPreferences.edit().putString("user_id", data.getString("user_id")).apply()

                                sharedPreferences.edit().putString("name",data.getString("name")).apply()

                                sharedPreferences.edit().putString("email",data.getString("email")).apply()

                                sharedPreferences.edit().putString("mobile_number",data.getString("mobile_number")).apply()

                                sharedPreferences.edit().putString("address",data.getString("address")).apply()

                                Toast.makeText(this,"Registered Successfully",Toast.LENGTH_SHORT).show()

                                openHome()

                            }else{

                                val responseError = response.getString("errorMessage")

                                Toast.makeText(this, responseError.toString() ,Toast.LENGTH_SHORT).show()

                            }

                        },

                        Response.ErrorListener {

                            Toast.makeText(this,"Some Error Occurred ! $it",Toast.LENGTH_SHORT).show()

                        }){

                        override fun getHeaders(): MutableMap<String, String> {

                            val headers = HashMap<String,String>()
                            headers["Content-type"]="application/json"
                            headers["token"]="136105b06fe1b4"
                            return headers

                        }

                    }

                    queue.add(jsonObjectRequest)

                }catch (e : JSONException){

                    Toast.makeText(this,"Some Unexpected Error Occurred !",Toast.LENGTH_SHORT).show()


                }

            }else{

                Toast.makeText(this,"Check Input Fields and Try Again",Toast.LENGTH_SHORT).show()

            }

        }else{


            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found !")
            dialog.setPositiveButton("Open Setting"){ _, _ ->

                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)


            }
            dialog.setNegativeButton("Exit"){ _, _ ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()

        }

    }

    private fun openHome() {

        val i = Intent(this@SignUpActivity,MainActivity::class.java)
        startActivity(i)
        finish()

    }

    private fun noErrors():Boolean{

        var noError = 0
        val name = etName.text.toString()
        val mobile = etMobile.text.toString()
        val mail = etMail.text.toString()
        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        val add = etAdd.text.toString()
        val pass = etPass.text.toString()
        val cPass = etCPass.text.toString()

        if(name.isBlank()){

            etName.error = "Field Missing"

        }else{

            noError++

        }


        when {
            mobile.isBlank() -> {

                etMobile.error = "Field Missing"

            }
            mobile.length < 10 -> {

                etMobile.error = "Mobile Number Should Be Of 10 Digits"

            }
            else -> {

                noError++

            }
        }

        if(mail.isBlank())
        {
            etMail.error = "Field Missing"

        }else if( !mail.trim().matches(emailPattern) ){

            etMail.error= "Invalid Email"

        }else{

            noError++

        }

        if(add.isBlank())
        {
            etAdd.error = "Field Missing"
        }else{

            noError++

        }

        when {
            pass.isBlank() -> {

                etPass.error = "Field Missing"

            }
            pass.length < 4 -> {

                etPass.error = "Minimum 4 Characters Required"
            }
            else -> {

                noError++

            }
        }

        when {
            cPass.isBlank() -> {

                etCPass.error = "Field Missing"

            }
            cPass.length < 4 -> {

                etCPass.error = "Minimum 4 Characters Required"

            }
            pass != cPass -> {

                etPass.error="Password and Confirm Password Don't Match"
                etCPass.error="Password and Confirm Password Don't Match"

            }
            else -> {

                noError++

            }
        }

        return noError == 6

    }

    override fun onResume() {
        if(!ConnectionManager().checkConnectivity(this))
        {

            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found !")
            dialog.setPositiveButton("Open Setting"){ _, _ ->

                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)

            }
            dialog.setNegativeButton("Exit"){ _, _ ->

                ActivityCompat.finishAffinity(this)

            }
            dialog.setCancelable(false)

            dialog.create()
            dialog.show()

        }
        super.onResume()
    }
}