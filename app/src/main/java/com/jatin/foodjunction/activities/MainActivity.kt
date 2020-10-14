package com.jatin.foodjunction.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.jatin.foodjunction.R
import com.jatin.foodjunction.fragments.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var coordinatorLayout: CoordinatorLayout

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    private lateinit var frameLayout: FrameLayout

    private lateinit var navigationView: NavigationView

    private lateinit var textViewNameHeader : TextView

    private lateinit var textViewMobileHeader : TextView

    private lateinit var sharedPreferences: SharedPreferences

    private var previousMenuItem : MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)

        coordinatorLayout = findViewById(R.id.coordinatorLayout)

        toolbar = findViewById(R.id.toolbar)

        frameLayout = findViewById(R.id.frame)

        navigationView = findViewById(R.id.navigationView)

        val headerView = navigationView.getHeaderView(0)

        textViewNameHeader = headerView.findViewById(R.id.tvNameHeader)

        textViewMobileHeader = headerView.findViewById(R.id.tvMobileHeader)

        sharedPreferences = getSharedPreferences("RegistrationPreferences", Context.MODE_PRIVATE)

        textViewNameHeader.text = sharedPreferences.getString("name","Default")

        val mobile = "+91-" + sharedPreferences.getString("mobile_number","+91-9999999999")

        textViewMobileHeader.text = mobile

        setUpToolbar()

        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if(previousMenuItem != null){
                previousMenuItem ?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId)
            {
                R.id.home -> {

                    openHome()
                    drawerLayout.closeDrawers()

                }

                R.id.myProfile -> {

                    openProfile()
                    drawerLayout.closeDrawers()

                }

                R.id.favRes -> {

                    openFav()
                    drawerLayout.closeDrawers()

                }

                R.id.orderHistory ->{

                    openOrderHistory()
                    drawerLayout.closeDrawers()

                }

                R.id.faq -> {

                    openFAQ()
                    drawerLayout.closeDrawers()

                }

                R.id.logOut -> {

                    //Toast.makeText(this@MainActivity, "Clicked On Logout",Toast.LENGTH_SHORT).show()
                    logOut()
                    drawerLayout.closeDrawers()

                }

            }

            return@setNavigationItemSelectedListener true

        }


    }

    private fun setUpToolbar(){

        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if(id == android.R.id.home){

            drawerLayout.openDrawer(GravityCompat.START)

        }

        return super.onOptionsItemSelected(item)
    }

    private fun openHome(){

        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.frame,fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)

    }

    private fun openProfile(){

        val fragment = ProfileFragment(this)
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.frame,fragment)
        transaction.commit()

        supportActionBar?.title = "My Profile"
    }

    private fun openFav(){

        val fragment = FavouriteFragment()
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.frame,fragment)
        transaction.commit()

        supportActionBar?.title = "Favorite Restaurants"
    }

    private fun openOrderHistory(){

        val fragment = OrderHistoryFragment()
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.frame,fragment)
        transaction.commit()

        supportActionBar?.title = "My Previous Orders"
    }

    private fun openFAQ(){

        val fragment = FAQFragment()
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.frame,fragment)
        transaction.commit()

        supportActionBar?.title = "Frequently Asked Questions"

    }

    private fun logOut()
    {
        drawerLayout.closeDrawers()

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Confirmation")
        dialog.setMessage("Are you sure you want to Log Out?")
        dialog.setPositiveButton("Yes"){ _, _ ->

            val i = Intent(this@MainActivity,LoginActivity::class.java)
            sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()
            //ActivityCompat.finishAffinity(this)
            startActivity(i)
            finish()

        }
        dialog.setNegativeButton("No"){ _, _ ->
            openHome()
            drawerLayout.closeDrawers()
        }
        dialog.create()
        dialog.show()
    }

    override fun onBackPressed() {

        when(supportFragmentManager.findFragmentById(R.id.frame)){

            !is HomeFragment -> openHome()

            else -> super.onBackPressed()


        }

    }
}