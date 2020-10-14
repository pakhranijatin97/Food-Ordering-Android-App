package com.jatin.foodjunction.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jatin.foodjunction.R
import com.jatin.foodjunction.model.CartItem

class CartRecyclerAdapter(val context: Context, private val cartItems :ArrayList<CartItem>):
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    class CartViewHolder(view : View):RecyclerView.ViewHolder(view){

        val itemName : TextView = view.findViewById(R.id.itemName)

        val itemPrice : TextView = view.findViewById(R.id.itemPrice)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row,parent,false)

        return CartViewHolder(view)

    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        val cartItemObject= cartItems[position]
        holder.itemName.text =cartItemObject.foodName
        val cost ="Rs. "+cartItemObject.foodCost
        holder.itemPrice.text = cost

    }


}