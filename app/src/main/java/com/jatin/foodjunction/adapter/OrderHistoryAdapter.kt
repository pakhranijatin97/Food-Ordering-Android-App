package com.jatin.foodjunction.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.jatin.foodjunction.R
import com.jatin.foodjunction.model.CartItem
import com.jatin.foodjunction.model.OrderHistory
import com.jatin.foodjunction.util.ConnectionManager
import org.json.JSONException

class OrderHistoryAdapter(val context : Context, private val orderList : ArrayList<OrderHistory> ):
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolderOrderHistory>() {

    class ViewHolderOrderHistory(view: View) : RecyclerView.ViewHolder(view){

        val textViewRestaurantName : TextView = view.findViewById(R.id.txtViewOHSResName)
        val textViewDate : TextView = view.findViewById(R.id.txtViewOHSDate)
        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerOHS)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOrderHistory {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_single_row,parent,false)

        return ViewHolderOrderHistory(view)
    }

    override fun getItemCount(): Int {
        return  orderList.size
    }

    override fun onBindViewHolder(holder: ViewHolderOrderHistory, position: Int) {

        val orderObject = orderList[position]

        holder.textViewRestaurantName.text = orderObject.restaurantName

        var date = orderObject.orderPlacedOn
        date = date.replace("-","/")
        date = date.substring(0,6)+"20" + date.substring(6,8)
        holder.textViewDate.text = date

        val layoutManager =LinearLayoutManager(context)

        var orderAdapter : CartRecyclerAdapter

        if(ConnectionManager().checkConnectivity(context)){

            try{

                val orderItems = ArrayList<CartItem>()

                val sharedPreferences = context.getSharedPreferences("RegistrationPreferences",Context.MODE_PRIVATE)

                val userId = sharedPreferences.getString("user_id","0")

                val queue =  Volley.newRequestQueue(context)

                //val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"
                val url = "http://192.168.43.212/foodrunner_api_scripts/v2/orders/fetch_result.php?userId=$userId"

                val jsonObjectRequest = object  : JsonObjectRequest(Method.GET,url,null,Response.Listener {

                    val response =it.getJSONObject("data")

                    val success = response.getBoolean("success")

                    if(success){

                        val data = response.getJSONArray("data")

                        val restaurantObject = data.getJSONObject(position)

                        orderItems.clear()

                        val foodOrderedArray = restaurantObject.getJSONArray("food")

                        for(k in 0 until foodOrderedArray.length()){

                            val foodItem = foodOrderedArray.getJSONObject(k)
                            val itemObject = CartItem(
                                foodItem.getString("food_item_id"),
                                foodItem.getString("name"),
                                foodItem.getString("cost"),"0"
                                )

                            orderItems.add(itemObject)
                        }

                        orderAdapter = CartRecyclerAdapter(context,orderItems)

                        holder.recyclerView.adapter =orderAdapter

                        holder.recyclerView.layoutManager = layoutManager
                    }

                },Response.ErrorListener {

                    Toast.makeText(context,"Some Error Occurred",Toast.LENGTH_SHORT).show()

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
                Toast.makeText(context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
            }

        }

    }

}