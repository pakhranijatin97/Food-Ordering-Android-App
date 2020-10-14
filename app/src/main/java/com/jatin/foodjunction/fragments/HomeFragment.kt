package com.jatin.foodjunction.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.jatin.foodjunction.R
import com.jatin.foodjunction.adapter.HomeRecyclerAdapter
import com.jatin.foodjunction.model.Restaurant
import com.jatin.foodjunction.util.ConnectionManager
import kotlinx.android.synthetic.main.sort_buttons.view.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    private lateinit var recyclerHome : RecyclerView

    private lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter : HomeRecyclerAdapter

    lateinit var progressLayout : RelativeLayout

    lateinit var progressBar: ProgressBar

    private lateinit var sortView : View

    var restaurantInfoList = arrayListOf<Restaurant>()


    private var costComparator = Comparator<Restaurant>{ restaurant1, restaurant2 ->

        restaurant1.restaurantCostForOne.compareTo(restaurant2.restaurantCostForOne,true)

    }

    private var ratingComparator = Comparator<Restaurant>{ restaurant1, restaurant2 ->

        if(restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true) == 0){

            restaurant1.restaurantName.compareTo(restaurant2.restaurantName,true)

        }else{
            restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        recyclerHome = view.findViewById(R.id.recyclerHome)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility=View.VISIBLE

        layoutManager = LinearLayoutManager(activity)

        recyclerAdapter = HomeRecyclerAdapter(activity as Context,restaurantInfoList)

        recyclerHome.adapter = recyclerAdapter

        recyclerHome.layoutManager = layoutManager

        setHasOptionsMenu(true)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://192.168.43.212/foodrunner_api_scripts/v2/restaurants/fetch_result.php"
        if(ConnectionManager().checkConnectivity(activity as Context)){

            try {

            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                    progressLayout.visibility = View.GONE

                    val data=it.getJSONObject("data")

                    val success = data.getBoolean("success")

                    if (success){

                        val resArray = data.getJSONArray("data")
                        for (i in 0 until resArray.length()){
                            val restaurantJsonObject = resArray.getJSONObject(i)
                            val restaurantObject = Restaurant(

                                restaurantJsonObject.getString("id"),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("cost_for_one"),
                                restaurantJsonObject.getString("image_url")


                            )

                            restaurantInfoList.add(restaurantObject)

                        }

                        recyclerAdapter.notifyDataSetChanged()

                    }else{

                        Toast.makeText(activity as Context,"Some Error Occurred !", Toast.LENGTH_SHORT).show()

                    }

                    progressLayout.visibility =  View.GONE
                    progressBar.visibility= View.GONE


            }, Response.ErrorListener {

                progressLayout.visibility = View.GONE
                progressBar.visibility = View.GONE

                if(activity!=null){
                    Toast.makeText(activity as Context,"Volley Error Occurred !", Toast.LENGTH_SHORT).show()
                }


            }){

                override fun getHeaders(): MutableMap<String, String> {

                    val headers = HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="136105b06fe1b4"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)

            }catch (e:JSONException){

                Toast.makeText(activity as Context,"Some Unexpected Error Occurred !", Toast.LENGTH_SHORT).show()

            }
        }else
        {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found !")
            dialog.setPositiveButton("Open Setting"){ _, _ ->

                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)

            }
            dialog.setNegativeButton("Exit"){ _, _ ->
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {

            R.id.action_sort -> {

                sortView = View.inflate(context, R.layout.sort_buttons, null)
                AlertDialog.Builder(activity as Context).setTitle("Sort By?").setView(sortView)
                        .setPositiveButton("Ok") { _, _ ->
                            if (sortView.radioHighToLow.isChecked) {
                                Collections.sort(restaurantInfoList, costComparator)
                                restaurantInfoList.reverse()
                                recyclerAdapter.notifyDataSetChanged()
                            }
                            if (sortView.radioLowToHigh.isChecked) {
                                Collections.sort(restaurantInfoList, costComparator)
                                recyclerAdapter.notifyDataSetChanged()
                            }
                            if (sortView.radioRating.isChecked) {
                                Collections.sort(restaurantInfoList, ratingComparator)
                                restaurantInfoList.reverse()
                                recyclerAdapter.notifyDataSetChanged()
                            }

                        }
                        .setNegativeButton("Cancel") { _, _ ->

                        }.create().show()

            }
        }

        return super.onOptionsItemSelected(item)
    }


}