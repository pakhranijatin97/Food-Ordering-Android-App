package com.jatin.foodjunction.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.jatin.foodjunction.R
import com.jatin.foodjunction.adapter.CartRecyclerAdapter
import com.jatin.foodjunction.model.CartItem
import com.jatin.foodjunction.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class CartActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var textResName : TextView

    lateinit var recyclerCart : RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var cartAdapter : CartRecyclerAdapter

    private lateinit var progressLayout : RelativeLayout

    private lateinit var progressBar: ProgressBar

    private lateinit var llCart : LinearLayout

    lateinit var btnPlaceOrder : Button

    private var resId : String? = null

    private var resName: String? =null

    lateinit var selectedItem : ArrayList<String>

    var totalCost = 0

    var cartItems = arrayListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val bundle: Bundle? = intent.extras

        resId = bundle?.getString("resId")
        resName = bundle?.getString("resName").toString()
        selectedItem = bundle?.getStringArrayList("selectedItemIds") as ArrayList<String>
       // selectedItem = intent.getStringArrayListExtra("selectedItemIds")

        toolbar = findViewById(R.id.toolbarCart)

        textResName = findViewById(R.id.txtResNameCart)

        recyclerCart = findViewById(R.id.recyclerCart)

        layoutManager = LinearLayoutManager(this)

        progressLayout = findViewById(R.id.progressLayoutCart)

        progressBar = findViewById(R.id.progressBarCart)

        llCart = findViewById(R.id.llCart)

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        textResName.text = resName

        setToolbar()

        getData()

        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }


    }

    private fun getData(){

        if(ConnectionManager().checkConnectivity(this)){

            progressLayout.visibility = View.VISIBLE

            try {

                val queue = Volley.newRequestQueue(this)
                //val url = "http://13.235.250.119/v2/restaurants/fetch_result/$resId"
                val url = "http://192.168.43.212/foodrunner_api_scripts/v2/restaurants/fetch_result/fetch_with_restaurant_id.php?restaurantId=$resId"

                val jsonObjectRequest = object : JsonObjectRequest(Method.GET,url,null,Response.Listener {

                    val response = it.getJSONObject("data")
                    val success = response.getBoolean("success")

                    if(success){

                        val data = response.getJSONArray("data")

                        cartItems.clear()
                        totalCost = 0

                        for (i in 0 until data.length()){

                            val cartItemObject = data.getJSONObject(i)

                                if(selectedItem.contains(cartItemObject.getString("id"))){

                                    val menuObject = CartItem(
                                        cartItemObject.getString("id"),
                                        cartItemObject.getString("name"),
                                        cartItemObject.getString("cost_for_one"),
                                        cartItemObject.getString("restaurant_id")
                                    )
                                    totalCost += cartItemObject.getString("cost_for_one").toInt()
                                    cartItems.add(menuObject)
                                }

                            cartAdapter= CartRecyclerAdapter(this,cartItems)

                            recyclerCart.adapter = cartAdapter

                            recyclerCart.layoutManager = layoutManager

                        }

                        val stringCost = "Place Order ( Total : Rs. $totalCost )"
                        btnPlaceOrder.text = stringCost
                    }

                    progressLayout.visibility = View.GONE

                },Response.ErrorListener {

                    Toast.makeText(this,"Some Unexpected Error Occurred",Toast.LENGTH_SHORT).show()

                    progressLayout.visibility= View.GONE

                }){

                    override fun getHeaders(): MutableMap<String, String> {

                        val headers = HashMap<String,String>()
                        headers["Content-type"]="application/json"
                        headers["token"]="136105b06fe1b4"
                        return headers

                    }
                }
                queue.add(jsonObjectRequest)

            }catch (e: JSONException){

                Toast.makeText(this,"Some Unexpected Error Occurred",Toast.LENGTH_SHORT).show()

            }


        }else{



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

    }

    private fun placeOrder(){

        val sharedPreferences = this.getSharedPreferences("RegistrationPreferences", Context.MODE_PRIVATE)
        if(ConnectionManager().checkConnectivity(this)){

            progressLayout.visibility = View.VISIBLE

            try {

                val foodJSONArray = JSONArray()

                for(item in selectedItem){

                    val itemObject = JSONObject()
                    itemObject.put("food_item_id",item)

                    foodJSONArray.put(itemObject)

                }

                val placeOrder = JSONObject()
                placeOrder.put("user_id",sharedPreferences.getString("user_id","0"))
                placeOrder.put("restaurant_id",resId)
                placeOrder.put("total_cost",totalCost)
                placeOrder.put("food",foodJSONArray)

                val queue = Volley.newRequestQueue(this)
                //val url = "http://13.235.250.119/v2/place_order/fetch_result/"
                val url = "http://192.168.43.212/foodrunner_api_scripts/v2/place_order/fetch_result.php"

                val jsonObjectRequest = object :JsonObjectRequest(Method.POST,url,placeOrder,Response.Listener {

                    val response = it.getJSONObject("data")
                    val success = response.getBoolean("success")

                    if(success){

                        Toast.makeText(this,"Order Placed",Toast.LENGTH_SHORT).show()

                        val intent = Intent(this,OrderPlaced::class.java)
                        startActivity(intent)
                        finishAffinity()

                    }else{

                        val errorMessage = response.getString("errorMessage")
                        Toast.makeText(this, "Fail: $errorMessage",Toast.LENGTH_SHORT).show()
                    }

                    progressLayout.visibility = View.GONE

                },Response.ErrorListener {

                    Toast.makeText(this,"Some Error Occurred",Toast.LENGTH_SHORT).show()

                    progressLayout.visibility = View.GONE

                }){

                    override fun getHeaders(): MutableMap<String, String> {

                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "136105b06fe1b4"
                        return headers
                    }

                }
                queue.add(jsonObjectRequest)

            }catch (e: JSONException){
                Toast.makeText(this, "Some Exception Occurred :$e",Toast.LENGTH_SHORT).show()
            }


        }else{

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

    }

    private fun setToolbar(){

        setSupportActionBar(toolbar)
        supportActionBar?.title ="MyCart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            android.R.id.home -> {

                super.onBackPressed()
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