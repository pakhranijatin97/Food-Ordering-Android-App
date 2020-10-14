package com.jatin.foodjunction.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.jatin.foodjunction.R
import com.jatin.foodjunction.adapter.RestaurantDetailsRecyclerAdapter
import com.jatin.foodjunction.model.FoodItem
import com.jatin.foodjunction.util.ConnectionManager
import org.json.JSONException

class RestaurantDetailsActivity : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    lateinit var recyclerView: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var resMenuAdapterDetails : RestaurantDetailsRecyclerAdapter

    lateinit var btnProceed :Button

    lateinit var progressLayout : RelativeLayout

    private var resId : String? = null

    private var resName: String? =null

    lateinit var progressBar : ProgressBar


    var menuList = arrayListOf<FoodItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_new)

        toolbar = findViewById(R.id.toolbarResMenu)

        recyclerView = findViewById(R.id.recyclerResMenu)

        layoutManager = LinearLayoutManager(this)

        progressLayout = findViewById(R.id.progressLayoutResMenu)

        progressBar = findViewById(R.id.progressBarResMenu)

        btnProceed = findViewById(R.id.btnProceedResMenu)


        val bundle: Bundle? = intent.extras

        resId = bundle?.getString("resId")
        resName = bundle?.getString("resName")

        setToolbar(resName)
        getData(resId,resName)

    }

    private fun getData(resId: String?, resName: String?) {

        if(ConnectionManager().checkConnectivity(this)){

            progressLayout.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE

            try{

                val queue = Volley.newRequestQueue(this)

                //val url = "http://13.235.250.119/v2/restaurants/fetch_result/$resId"
                val url = "http://192.168.43.212/foodrunner_api_scripts/v2/restaurants/fetch_result/fetch_with_restaurant_id.php?restaurantId=$resId"

                val jsonObjectRequest = object : JsonObjectRequest(Method.GET,url,null,Response.Listener {

                    val response = it.getJSONObject("data")
                    val success = response.getBoolean("success")

                    if(success){

                        menuList.clear()

                        val data = response.getJSONArray("data")

                        for (i in 0 until data.length()){

                            val menuJSONObject = data.getJSONObject(i)
                            val menuObject = FoodItem(
                                menuJSONObject.getString("id"),
                                menuJSONObject.getString("name"),
                                menuJSONObject.getString("cost_for_one")
                            )
                            menuList.add(menuObject)

                            resMenuAdapterDetails = RestaurantDetailsRecyclerAdapter(this,menuList,resId,resName,btnProceed)

                            recyclerView.adapter = resMenuAdapterDetails

                            recyclerView.layoutManager =layoutManager

                        }


                    }

                    progressLayout.visibility = View.INVISIBLE
                    progressBar.visibility = View.INVISIBLE

                },Response.ErrorListener {

                    Toast.makeText(this,"Some Error Occurred",Toast.LENGTH_SHORT).show()

                    progressLayout.visibility = View.GONE
                    progressBar.visibility = View.GONE

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

                Toast.makeText(this,"Some Error Occurred", Toast.LENGTH_SHORT).show()

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

    private fun setToolbar(resName: String?) {

        setSupportActionBar(toolbar)
        supportActionBar?.title = resName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

    }

    override fun onBackPressed() {

        if(resMenuAdapterDetails.getSelectedItemCount() > 0){

            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Alert !")
            dialog.setMessage("Going back will remove everything from cart !")
            dialog.setPositiveButton("Yes"){ _, _ ->
                super.onBackPressed()
            }
            dialog.setNegativeButton("No"){ _, _ ->

            }
            dialog.create()
            dialog.show()

        }else{
            super.onBackPressed()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            android.R.id.home -> {

                if(resMenuAdapterDetails.getSelectedItemCount() > 0){

                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Alert !")
                    dialog.setMessage("Going back will remove everything from cart !")
                    dialog.setPositiveButton("Yes"){ _, _ ->
                        super.onBackPressed()
                    }
                    dialog.setNegativeButton("No"){ _, _ ->

                    }
                    dialog.create()
                    dialog.show()

                }else{
                    super.onBackPressed()
                }

            }
        }

        return super.onOptionsItemSelected(item)
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