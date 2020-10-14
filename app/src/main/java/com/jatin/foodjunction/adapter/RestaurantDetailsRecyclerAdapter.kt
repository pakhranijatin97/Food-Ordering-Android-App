package com.jatin.foodjunction.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jatin.foodjunction.R
import com.jatin.foodjunction.activities.CartActivity
import com.jatin.foodjunction.model.FoodItem

class RestaurantDetailsRecyclerAdapter(val context : Context, private val itemList : ArrayList<FoodItem>, private val restId : String?, private val restName : String?, private val btnProceed : Button) : RecyclerView.Adapter<RestaurantDetailsRecyclerAdapter.MenuViewHolder>() {


    private var itemSelected:Int =0

    private var itemSelectedIds = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_menu_item,parent,false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        itemList.size
        return itemList.size
    }


    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val foodItem = itemList[position]

        holder.srNo.text = (position + 1).toString()

        holder.txtItemName.text = foodItem.foodItemName

        val costForOne = "Rs. " + foodItem.FoodCostForOne
        holder.txtItemCost.text = costForOne

        holder.addToCart.setOnClickListener {

            itemSelected++

            itemSelectedIds.add(foodItem.foodItemId)

            holder.addToCart.visibility = View.GONE
            holder.removeFromCart.visibility=View.VISIBLE

            if(itemSelected > 0){

                btnProceed.visibility = View.VISIBLE

            }else{

                btnProceed.visibility = View.GONE

            }
            Log.i("Button clicked",""+foodItem.foodItemId)

        }

        holder.removeFromCart.setOnClickListener {

            itemSelected--

            itemSelectedIds.remove(foodItem.foodItemId)

            holder.removeFromCart.visibility = View.GONE
            holder.addToCart.visibility = View.VISIBLE

            if(itemSelected > 0){

                btnProceed.visibility = View.VISIBLE

            }else{

                btnProceed.visibility = View.GONE

            }

        }

        btnProceed.setOnClickListener {

            val intent = Intent(context, CartActivity::class.java)

            intent.putExtra("resId",restId)
            intent.putExtra("resName",restName)
            intent.putExtra("selectedItemIds",itemSelectedIds)

            context.startActivity(intent)

        }

        if(itemSelected > 0){

            btnProceed.visibility = View.VISIBLE

        }else{

            btnProceed.visibility = View.GONE

        }

    }

    class MenuViewHolder(view: View): RecyclerView.ViewHolder(view){
        val srNo : TextView = view.findViewById(R.id.txtSrNo)
        val txtItemName : TextView = view.findViewById(R.id.txtItemName)
        val txtItemCost : TextView = view.findViewById(R.id.txtItemCost)
        val addToCart : Button = view.findViewById(R.id.btnAddToCart)
        val removeFromCart : Button = view.findViewById(R.id.btnRemoveFromCart)

    }

    fun getSelectedItemCount():Int{
        return itemSelected
    }

}