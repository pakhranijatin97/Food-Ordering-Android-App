package com.jatin.foodjunction.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.jatin.foodjunction.R
import com.jatin.foodjunction.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPassword : AppCompatActivity() {

    private lateinit var btnNext : Button
    private lateinit var etMobile : EditText
    private lateinit var etMail : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        supportActionBar?.hide()

        etMobile = findViewById(R.id.etMobileFPass)

        etMail = findViewById(R.id.etMailFPass)

        btnNext = findViewById(R.id.btnNextFPass)

        btnNext.setOnClickListener{

                //val i = Intent(this@ForgotPassword, ResetPassword::class.java)
                //startActivity(i)

            if(ConnectionManager().checkConnectivity(this)){

                if(noError()){

                    println("No Errors")

                    try {

                            val user =JSONObject()

                            user.put("mobile_number",etMobile.text.toString())
                            user.put("email",etMail.text.toString())

                            val queue =Volley.newRequestQueue(this)
                            val url = "http://192.168.43.212/foodrunner_api_scripts/v2/forgot_password/fetch_result.php"

                            val jsonObjectRequest = object :JsonObjectRequest(Method.POST,url,user,Response.Listener {

                            val response = it.getJSONObject("data")

                            val success = response.getBoolean("success")

                            if(success){

                                val firstTry = response.getBoolean("first_try")

                                if(firstTry){

                                    Toast.makeText(this,"OTP Sent !",Toast.LENGTH_SHORT).show()

                                    resetPassword()

                                }else{

                                    Toast.makeText(this,"OTP Already Sent !",Toast.LENGTH_SHORT).show()

                                    resetPassword()

                                }


                            }else{

                                val errorMessage = response.getString("errorMessage")
                                Toast.makeText(this,errorMessage.toString(),Toast.LENGTH_SHORT).show()

                                }


                            },Response.ErrorListener {

                                Toast.makeText(this,"Some Error Occurred ! $it",Toast.LENGTH_SHORT).show()

                                }){

                                override fun getHeaders(): MutableMap<String, String> {

                                    val headers = HashMap<String,String>()
                                    headers["Content-type"]="application/json"
                                    headers["token"]="136105b06fe1b4"
                                    return headers

                                }
                            }

                        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(15000,1,1f)

                        queue.add(jsonObjectRequest)

                    }catch (e: JSONException){

                        Toast.makeText(this,"Some Unexpected Error Occurred",Toast.LENGTH_SHORT).show()

                    }

                }else{

                    Toast.makeText(this,"Check Input Fields and Try Again", Toast.LENGTH_SHORT).show()

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
    }

    fun resetPassword(){

        val i =Intent(this@ForgotPassword,ResetPassword::class.java)
        i.putExtra("mobile_number", etMobile.text.toString() )
        startActivity(i)
        //finish()


    }

    private fun noError():Boolean{

        var noError = 0

        val mobile = etMobile.text.toString()

        val mail = etMail.text.toString()

        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

        when {
            mobile.isBlank() -> {

                etMobile.error ="Missing Field"

            }
            mobile.length < 10 -> {
                etMobile.error = "Minimum 10 Digits Required"
            }
            else -> {
                noError++
            }
        }

        if(mail.isBlank()){

            etMail.error="Missing Field"

        }else if( !mail.trim().matches(emailPattern) ){

            etMail.error = "Invalid Email"

        }else{
            noError++
        }

        return noError==2

    }

    override fun onResume() {
        if(!ConnectionManager().checkConnectivity(this)) {

            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found !")
            dialog.setPositiveButton("Open Setting") { _, _ ->

                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)


            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()

        }
        super.onResume()
    }
}