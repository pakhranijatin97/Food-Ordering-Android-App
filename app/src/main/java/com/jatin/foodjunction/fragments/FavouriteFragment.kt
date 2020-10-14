package com.jatin.foodjunction.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.jatin.foodjunction.R
import com.jatin.foodjunction.adapter.HomeRecyclerAdapter
import com.jatin.foodjunction.database.RestaurantDatabase
import com.jatin.foodjunction.database.RestaurantEntity
import com.jatin.foodjunction.model.Restaurant
import com.jatin.foodjunction.util.ConnectionManager
import org.json.JSONException


class FavouriteFragment : Fragment() {

    private lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout : RelativeLayout
    lateinit var progressBar: ProgressBar
    private lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var textViewEmpty :TextView
    lateinit var relativeLayoutEmpty :RelativeLayout
    lateinit var imgHeart : ImageView
    lateinit var restaurantList : ArrayList<Restaurant>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFav)
        progressLayout = view.findViewById(R.id.progressLayoutFav)
        progressBar = view.findViewById(R.id.progressBarFav)

        textViewEmpty = view.findViewById(R.id.textViewEmpty)
        relativeLayoutEmpty = view.findViewById(R.id.relativeLayoutEmpty)
        imgHeart = view.findViewById(R.id.imgHeartFav)

        layoutManager = LinearLayoutManager(activity)

        restaurantList = arrayListOf()

        recyclerAdapter = HomeRecyclerAdapter(activity as Context,restaurantList)

        recyclerFavourite.adapter = recyclerAdapter

        recyclerFavourite.layoutManager = layoutManager

        getData()

        return view
    }

    private fun getData(){

        if(ConnectionManager().checkConnectivity(activity as Context)){

            progressLayout.visibility = View.VISIBLE

            try{

                val queue = Volley.newRequestQueue(activity as Context)

                //val url ="http://13.235.250.119/v2/restaurants/fetch_result/"
                 val url="http://192.168.43.212/foodrunner_api_scripts/v2/restaurants/fetch_result.php"
                val jsonObjectRequest = object :JsonObjectRequest(Method.GET,url,null,Response.Listener {

                    val response = it.getJSONObject("data")

                    val success = response.getBoolean("success")

                    if(success){

                        restaurantList.clear()

                        val data = response.getJSONArray("data")

                        for(i in 0 until data.length()){

                            val restaurantJSONObject = data.getJSONObject(i)
                            val restaurantEntity = RestaurantEntity(
                                restaurantJSONObject.getInt("id"),
                                restaurantJSONObject.getString("name"),
                                restaurantJSONObject.getString("rating"),
                                restaurantJSONObject.getString("cost_for_one"),
                                restaurantJSONObject.getString("image_url")
                            )

                            if(DBASyncTask(activity as Context,restaurantEntity,1).execute().get()){

                                val restaurantObject = Restaurant(

                                    restaurantJSONObject.getString("id"),
                                    restaurantJSONObject.getString("name"),
                                    restaurantJSONObject.getString("rating"),
                                    restaurantJSONObject.getString("cost_for_one"),
                                    restaurantJSONObject.getString("image_url")

                                )

                                restaurantList.add(restaurantObject)

                                if (restaurantList.isEmpty()){

                                    textViewEmpty.visibility = View.VISIBLE
                                    relativeLayoutEmpty.visibility = View.VISIBLE
                                    imgHeart.visibility = View.VISIBLE

                                }else{

                                    textViewEmpty.visibility = View.GONE
                                    relativeLayoutEmpty.visibility = View.GONE
                                    imgHeart.visibility = View.GONE

                                }
                            }
                        }
                        recyclerAdapter.notifyDataSetChanged()

                    }else{
                        Toast.makeText(activity as Context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                    }

                    progressLayout.visibility = View.GONE
                    progressBar.visibility = View.GONE

                },Response.ErrorListener {

                    Toast.makeText(activity as Context,"Some Unexpected Error Occurred",Toast.LENGTH_SHORT).show()

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
                Toast.makeText(activity as Context,"Some Unexpected Error Occurred !", Toast.LENGTH_SHORT).show()
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

    }

    class DBASyncTask(val context: Context, private val restaurantEntity : RestaurantEntity, private val mode:Int) : AsyncTask<Void, Void, Boolean>(){

        private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurants-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            return when(mode){
                1 -> {
                    val restaurant :RestaurantEntity? = db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    restaurant !=null
                }
                else -> false
            }
        }

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