package com.example.yshopping.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.yshopping.R

class UserAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_user_account)
    }
}