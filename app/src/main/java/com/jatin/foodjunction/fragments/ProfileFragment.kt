package com.jatin.foodjunction.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jatin.foodjunction.R


class ProfileFragment(private val contextParameter : Context) : Fragment() {

    private lateinit var textViewName : TextView

    private lateinit var textViewMobile : TextView

    private lateinit var textViewEmail : TextView

    private lateinit var textViewAddress: TextView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_white, container, false)

        textViewName = view.findViewById(R.id.tvName)

        textViewMobile = view.findViewById(R.id.tvMobile)

        textViewEmail = view.findViewById(R.id.tvMail)

        textViewAddress = view.findViewById(R.id.tvAdd)

        sharedPreferences = contextParameter.getSharedPreferences("RegistrationPreferences",Context.MODE_PRIVATE)

        textViewName.text=sharedPreferences.getString("name","Default")

        textViewMobile.text = sharedPreferences.getString("mobile_number","+91-9999999999")

        textViewEmail.text = sharedPreferences.getString("email","default@foodrunner.com")

        textViewAddress.text = sharedPreferences.getString("address","Pune")


        return view
    }

}