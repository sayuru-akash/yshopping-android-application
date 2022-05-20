package com.example.yshopping.activities

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.yshopping.R
import com.example.yshopping.databinding.ActivityBottomNavBinding
import com.example.yshopping.utils.Constants

class BottomNavActivity : BaseActivity() {

    private lateinit var binding: ActivityBottomNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        //supportActionBar!!.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.button_bg))
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_bottom_nav)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_cart, R.id.navigation_account
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra(Constants.EXTRA_GO_TO_CART)) {
            val forceCart = intent.extras!!.getString(Constants.EXTRA_GO_TO_CART)!!
            if (forceCart == "true") {
                intent.removeExtra(Constants.EXTRA_GO_TO_CART)
                binding.navView.selectedItemId = R.id.navigation_cart
            }else{
                binding.navView.selectedItemId = R.id.navigation_home
            }
        }
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }

}