package com.jatin.foodjunction.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.jatin.foodjunction.R
import com.jatin.foodjunction.adapter.OrderHistoryAdapter
import com.jatin.foodjunction.model.OrderHistory
import com.jatin.foodjunction.util.ConnectionManager
import org.json.JSONException


class OrderHistoryFragment : Fragment() {

    lateinit var layoutManager : RecyclerView.LayoutManager

    lateinit var adapter : OrderHistoryAdapter

    lateinit var recyclerView: RecyclerView

    lateinit var progressLayout : RelativeLayout

    private lateinit var progressBar : ProgressBar

    lateinit var noOrderFirst : TextView
    lateinit var noOrderSecond : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerView = view.findViewById(R.id.recyclerOH)

        progressLayout = view.findViewById(R.id.progressLayoutOH)

        progressBar = view.findViewById(R.id.progressBarOH)

        layoutManager = LinearLayoutManager(activity)

        noOrderFirst = view.findViewById(R.id.textViewNoOrderFir)

        noOrderSecond = view.findViewById(R.id.textViewNoOrderSec)

        val orderList = ArrayList<OrderHistory>()

        val sharedPreferences = this.activity?.getSharedPreferences("RegistrationPreferences", Context.MODE_PRIVATE)

        val userId =sharedPreferences?.getString("user_id","0")

        if(ConnectionManager().checkConnectivity(activity as Context)){

            progressLayout.visibility = View.VISIBLE

            try{

                val queue = Volley.newRequestQueue(activity as Context)
                //val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"
                val url = "http://192.168.43.212/foodrunner_api_scripts/v2/orders/fetch_result.php?userId=$userId"

                val jsonObjectRequest = object :JsonObjectRequest(Method.GET,url,null,Response.Listener {

                    val response = it.getJSONObject("data")

                    val success = response.getBoolean("success")

                    if (success){

                        val data = response.getJSONArray("data")

                        if(data.length() == 0){

                            noOrderFirst.visibility = View.VISIBLE
                            noOrderSecond.visibility = View.VISIBLE

                        }else{

                            noOrderFirst.visibility = View.GONE
                            noOrderSecond.visibility = View.GONE

                            for (i in 0 until data.length()){

                                val restaurantObject = data.getJSONObject(i)

                                val eachRestaurantObject = OrderHistory(

                                    restaurantObject.getString("order_id"),
                                    restaurantObject.getString("restaurant_name"),
                                    restaurantObject.getString("total_cost"),
                                    restaurantObject.getString("order_placed_at")
                                        .substring(0,10)
                                )
                                orderList.add(eachRestaurantObject)

                                adapter = OrderHistoryAdapter(activity as Context,orderList)

                                recyclerView.adapter = adapter

                                recyclerView.layoutManager = layoutManager
                            }

                        }
                    }

                    progressLayout.visibility = View.GONE

                },Response.ErrorListener {

                    progressLayout.visibility = View.GONE
                    Toast.makeText(activity as Context,"Some Error Occurred",Toast.LENGTH_SHORT).show()


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
                Toast.makeText(activity as Context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
            }

        }else{

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found !")
            dialog.setPositiveButton("Open Setting") { _, _ ->

                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)


            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }

        return view
    }

    override fun onResume() {

        if(!ConnectionManager().checkConnectivity(activity as Context)) {

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found !")
            dialog.setPositiveButton("Open Setting") { _, _ ->

                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)


            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        super.onResume()
    }


}