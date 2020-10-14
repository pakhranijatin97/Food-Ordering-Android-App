package com.jatin.foodjunction.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jatin.foodjunction.R

class SplashScreen : AppCompatActivity() {

    private lateinit var topAnim : Animation

    private lateinit var  bottomAnim : Animation

    private lateinit var imgLogo: ImageView

    private lateinit var txtName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        imgLogo = findViewById(R.id.imgLogo)

        txtName = findViewById(R.id.txtName)

        topAnim = AnimationUtils.loadAnimation(this@SplashScreen,
            R.anim.top_animation
        )

        bottomAnim = AnimationUtils.loadAnimation(this@SplashScreen,
            R.anim.bottom_animation
        )

        imgLogo.startAnimation(topAnim)

        txtName.startAnimation(bottomAnim)

        Handler().postDelayed({ run{

            val i = Intent(this@SplashScreen,
                LoginActivity::class.java)
            startActivity(i)
            finish()

        } },2500)

    }
}