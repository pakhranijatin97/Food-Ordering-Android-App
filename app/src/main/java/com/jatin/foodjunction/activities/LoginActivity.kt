package com.jatin.foodjunction.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

class LoginActivity : AppCompatActivity() {

    private lateinit var txtNewUser : TextView

    private lateinit var txtForgotPass : TextView

    private lateinit var btnLogin : Button

    private lateinit var etMobileNumber : EditText

    private lateinit var etPassword : EditText

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        txtNewUser = findViewById(R.id.txtNewUserLogin)

        txtForgotPass = findViewById(R.id.txtForgotPassLogin)

        btnLogin = findViewById(R.id.btnLogin)

        etMobileNumber = findViewById(R.id.etMobileLogin)

        etPassword = findViewById(R.id.etPassLogin)

        sharedPreferences = getSharedPreferences("RegistrationPreferences", Context.MODE_PRIVATE)

        txtNewUser.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        txtForgotPass.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        txtNewUser.setOnClickListener {
            val i = Intent(this@LoginActivity,
                SignUpActivity::class.java)
            startActivity(i)
        }

        txtForgotPass.setOnClickListener{
            val i = Intent(this@LoginActivity,
                ForgotPassword::class.java)
            startActivity(i)
        }


        if(sharedPreferences.getBoolean("isLoggedIn",false)){

            userLoggedIn()

        }

       btnLogin.setOnClickListener {

            funUserLogin()

        }

    }

    private fun funUserLogin() {

        val sharedPreferences =getSharedPreferences("RegistrationPreferences",Context.MODE_PRIVATE)

        if(ConnectionManager().checkConnectivity(this)){

            if(noError()) {

                try {

                    val user = JSONObject()

                    user.put("mobile_number", etMobileNumber.text.toString())
                    user.put("password", etPassword.text.toString())

                    val queue = Volley.newRequestQueue(this)

                    //val url = "http://13.235.250.119/v2/login/fetch_result"

                    val url = "http://192.168.43.212/foodrunner_api_scripts/v2/login/fetch_result.php"


                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST, url, user, Response.Listener {

                            val response = it.getJSONObject("data")

                            val success = response.getBoolean("success")

                            if (success) {

                                val data = response.getJSONObject("data")


                                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                                sharedPreferences.edit()
                                    .putString("user_id", data.getString("user_id")).apply()

                                sharedPreferences.edit().putString("name", data.getString("name"))
                                    .apply()

                                sharedPreferences.edit().putString("email", data.getString("email"))
                                    .apply()

                                sharedPreferences.edit()
                                    .putString("mobile_number", data.getString("mobile_number"))
                                    .apply()

                                sharedPreferences.edit()
                                    .putString("address", data.getString("address")).apply()

                                Toast.makeText(
                                    this,
                                    "Welcome " + data.getString("name"),
                                    Toast.LENGTH_SHORT
                                ).show()

                                userLoggedIn()
                            } else {

                                //val responseMessage = response.getString("errorMessage")

                                Toast.makeText(this, /*responseMessage.toString()*/"Invalid Mobile Number or Password", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }, Response.ErrorListener {

                            Toast.makeText(this, "Some Volley Error Occurred", Toast.LENGTH_SHORT).show()

                        }

                        ) {

                            override fun getHeaders(): MutableMap<String, String> {

                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = ""
                                return headers
                            }

                        }
                    queue.add(jsonObjectRequest)

                } catch (exception: JSONException) {

                    Toast.makeText(this, "Some Exception Occurred", Toast.LENGTH_SHORT).show()

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

    fun userLoggedIn() {
        val intent =Intent(this@LoginActivity,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun noError(): Boolean {

        var noError = 0

        when {
            etMobileNumber.text.isBlank() -> {

                etMobileNumber.error = "Mobile Number Missing"

            }
            etMobileNumber.text.toString().length < 10 -> {

                etMobileNumber.error = "Minimum 10 Digits Required"

            }
            else -> {

                noError++

            }
        }

        when {
            etPassword.text.isBlank() -> {

                etPassword.error = "Password Missing"

            }
            etPassword.text.toString().length < 4 -> {

                etPassword.error = "Minimum 4 Characters Required"
            }
            else -> {

                noError++

            }
        }

        return noError==2

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