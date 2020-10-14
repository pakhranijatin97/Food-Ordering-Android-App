package com.jatin.foodjunction.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.jatin.foodjunction.R
import com.jatin.foodjunction.activities.RestaurantDetailsActivity
import com.jatin.foodjunction.database.RestaurantDatabase
import com.jatin.foodjunction.database.RestaurantEntity
import com.jatin.foodjunction.model.Restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context : Context, private var itemList : ArrayList<Restaurant>) : RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>(){

    class HomeViewHolder(view:View): RecyclerView.ViewHolder(view){
        val txtRestaurantName : TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantCostForOne : TextView = view.findViewById(R.id.txtRestaurantCostForOne)
        val txtRestaurantRating : TextView = view.findViewById(R.id.txtRestaurantRating)
        val imgRestaurantImage : ImageView = view.findViewById(R.id.imgRestaurantImage)
        val imgHeart : ImageView = view.findViewById(R.id.imgHeart)
        val llContent : LinearLayout = view.findViewById(R.id.llContent)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)

        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]

        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId.toInt(),
            restaurant.restaurantName,
            restaurant.restaurantRating,
            restaurant.restaurantCostForOne,
            restaurant.restaurantImage
        )

        val checkFav = DBASyncTask(context,restaurantEntity,1).execute()
        val isFav = checkFav.get()

        if(isFav)
        {
            holder.imgHeart.setImageResource(R.drawable.ic_heart_fill)
        }else{

            holder.imgHeart.setImageResource(R.drawable.ic_heart_outline)
        }

        holder.imgHeart.setOnClickListener{

            if(!DBASyncTask(context,restaurantEntity,1).execute().get()){

                val async = DBASyncTask(context,restaurantEntity,2).execute()
                val result = async.get()

                if(result)
                {
                    Toast.makeText(context,"Added To Favourites",Toast.LENGTH_SHORT).show()
                    holder.imgHeart.setImageResource(R.drawable.ic_heart_fill)
                }else{
                    Toast.makeText(context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                }

            }else{

                val async = DBASyncTask(context,restaurantEntity,3).execute()
                val result = async.get()

                if(result){

                    Toast.makeText(context,"Removed From Favourites",Toast.LENGTH_SHORT).show()
                    holder.imgHeart.setImageResource(R.drawable.ic_heart_outline)

                }else{
                    Toast.makeText(context,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                }

            }

        }
        holder.llContent.setOnClickListener {
            //Toast.makeText(context,"Clicked on ${holder.txtRestaurantName.text}",Toast.LENGTH_SHORT).show()

            val resId = restaurant.restaurantId

            val intent = Intent(context,RestaurantDetailsActivity::class.java)

            intent.putExtra("resId", resId)

            intent.putExtra("resName",holder.txtRestaurantName.text.toString() )

            context.startActivity(intent)

        }

        holder.txtRestaurantName.text = restaurant.restaurantName
        val costForOne = "Rs. " + restaurant.restaurantCostForOne+"/person"
        holder.txtRestaurantCostForOne.text =costForOne
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.restaurant_clipart).into(holder.imgRestaurantImage)

    }


    class DBASyncTask(val context: Context, private val restaurantEntity : RestaurantEntity, private val mode:Int) : AsyncTask<Void, Void, Boolean>(){

        private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurants-db").fallbackToDestructiveMigration().build()


        override fun doInBackground(vararg params: Void?): Boolean {


            when(mode)
            {
                1 -> {

                    val restaurant: RestaurantEntity?=db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return restaurant !=null

                }

                2 -> {

                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {

                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true

                }
            }

            return false

        }

    }

}