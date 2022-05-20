package com.example.yshopping.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.example.yshopping.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            //testing
            //startActivity(Intent(this, BottomNavActivity::class.java))
            //startActivity(Intent(this, AddProductActivity::class.java))
            finish()
        }, 600)
    }
}