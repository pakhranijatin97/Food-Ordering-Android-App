package com.jatin.foodjunction.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jatin.foodjunction.R

class OrderPlaced : AppCompatActivity() {

    private lateinit var topAnim : Animation

    private lateinit var  bottomAnim : Animation

    private lateinit var imgOrderPlaced : ImageView

    private lateinit var textViewOrderPlaced : TextView

    private lateinit var btnOk : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_order_placed)

        imgOrderPlaced = findViewById(R.id.imgOrderPlaced)

        textViewOrderPlaced = findViewById(R.id.tvOrderPlaced)

        btnOk = findViewById(R.id.btnOk)

        topAnim = AnimationUtils.loadAnimation(this@OrderPlaced,
            R.anim.top_animation
        )

        bottomAnim = AnimationUtils.loadAnimation(this@OrderPlaced,
            R.anim.bottom_animation
        )

        imgOrderPlaced.startAnimation(topAnim)

        textViewOrderPlaced.startAnimation(topAnim)

        btnOk.startAnimation(bottomAnim)


        btnOk.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

    }

    override fun onBackPressed() {

    }
}