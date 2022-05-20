package com.example.yshopping.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.yshopping.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_forgot_password)

        val log_in_text = findViewById<android.widget.TextView>(R.id.log_in_text)
        log_in_text.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val submit_button = findViewById<android.widget.Button>(R.id.submit_button)
        submit_button.setOnClickListener {
            if (validateMail()){
                showProgressDialog()
                resetPass()
            }
        }
    }

    private fun validateMail(): Boolean {
        val email = findViewById<android.widget.EditText>(R.id.login_email_text).text.toString()
        if(email.isEmpty()){
            showErrorSnackbar("Email is required", true)
            return false
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showErrorSnackbar("Email is invalid",true)
            return false
        }
        return true
    }
    private fun resetPass(){
        val email = findViewById<android.widget.EditText>(R.id.login_email_text).text.toString()

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->

                hideProgressDialog()

                if (task.isSuccessful) {
                    showErrorSnackbar("Email sent successfully. Please check your inbox or spam folder", false)
                    finish()
                } else {
                    showErrorSnackbar(task.exception!!.message.toString(), true)
                }
            }
    }
}