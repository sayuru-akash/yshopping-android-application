package com.example.yshopping.activities

import android.annotation.SuppressLint
import android.os.Bundle
import com.example.yshopping.R
import com.example.yshopping.utils.Constants

class MainActivity : BaseActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(Constants.YSHOPPING_PREFERENCES, MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!

    }
}