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
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.jatin.foodjunction.R
import com.jatin.foodjunction.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetPassword : AppCompatActivity() {

    private lateinit var etOTP : EditText

    private lateinit var etPass : EditText

    private lateinit var etCPass : EditText

    private lateinit var btnSubmit : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        supportActionBar?.hide()

        etOTP = findViewById(R.id.etOTPRPass)

        etPass = findViewById(R.id.etPassRPass)

        etCPass = findViewById(R.id.etCPassRPass)

        btnSubmit = findViewById(R.id.btnSubmitRPass)

        val bundle: Bundle? = intent.extras

        val mobileNumber = bundle?.getString("mobile_number")


        btnSubmit.setOnClickListener {

                if(ConnectionManager().checkConnectivity(this))
                {

                    if(noErrors()){

                        try {

                            val user = JSONObject()

                            user.put("mobile_number",mobileNumber)
                            user.put("password",etPass.text.toString())
                            user.put("otp",etOTP.text.toString())

                            val queue = Volley.newRequestQueue(this@ResetPassword)
                            val url = "http://192.168.43.212/foodrunner_api_scripts/v2/reset_password/fetch_result.php"

                            val jsonObjectRequest = object :JsonObjectRequest(Method.POST,url,user,Response.Listener {

                                val response = it.getJSONObject("data")
                                val success = response.getBoolean("success")

                                if(success){

                                    val successMessage = response.getString("successMessage")

                                    Toast.makeText(this,successMessage.toString(),Toast.LENGTH_SHORT).show()

                                    passChanged()

                                }else{

                                    val errorMessage = response.getString("errorMessage")
                                    Toast.makeText(this,errorMessage.toString(),Toast.LENGTH_SHORT).show()

                                }

                            },Response.ErrorListener {

                                Toast.makeText(this,"Some Error Occurred !",Toast.LENGTH_SHORT).show()

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

    private fun noErrors(): Boolean {

        var noError = 0

        val otp = etOTP.text.toString()
        val pass = etPass.text.toString()
        val cpass = etCPass.text.toString()

        if(otp.isBlank()){

            etOTP.error = "Missing Field"

        }else if(otp.length < 4 || otp.length >4){

            etOTP.error = "OTP  is of 4 Digits"

        }else{

            noError++

        }

        when {
            pass.isBlank() -> {

                etPass.error="Missing Field"

            }
            pass.length < 4 -> {

                etPass.error="Minimum 4 Characters Required"

            }
            else -> {

                noError++

            }
        }

        when {
            cpass.isBlank() -> {

                etCPass.error="Missing Field"

            }
            cpass.length < 4 -> {

                etCPass.error="Minimum 4 Characters Required"

            }
            pass != cpass -> {

                etPass.error = "Password and Confirm Password Don't Match"
                etCPass.error = "Password and Confirm Password Don't Match"

            }
            else -> {

                noError++

            }
        }

        return noError==3
    }

    fun passChanged(){

        val i = Intent(this@ResetPassword,LoginActivity::class.java)
        startActivity(i)
        finish()

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