package com.example.yshopping.activities

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.yshopping.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        application.setTheme(R.style.Theme_YShopping)
    }

    private var doubleBackToExitPressedOnce = false

    lateinit var yProgressDialog: Dialog

    fun showErrorSnackbar(message: String, errorMessage: Boolean) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view

        if (errorMessage) {
            sbView.setBackgroundColor(ContextCompat.getColor(this,R.color.colorSnackBarError))
        }else{
            sbView.setBackgroundColor(ContextCompat.getColor(this,R.color.colorSnackBarSuccess))
        }
        snackbar.show()
    }

    fun showProgressDialog() {
        yProgressDialog = Dialog(this)
        yProgressDialog.setContentView(R.layout.dialog_progress)
        yProgressDialog.setCancelable(false)
        yProgressDialog.setCanceledOnTouchOutside(false)
        yProgressDialog.show()
    }

    fun hideProgressDialog() {
        yProgressDialog.dismiss()
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        // Handler code should be executed after 1 second.
        @Suppress("DEPRECATION") val mHandler = Handler()
        mHandler.postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}