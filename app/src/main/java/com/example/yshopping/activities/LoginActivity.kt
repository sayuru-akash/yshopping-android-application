package com.example.yshopping.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.yshopping.R
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.User
import com.example.yshopping.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {

            showProgressDialog()
            FirestoreClass().getUserInfo(this)

        } else {

           val new_user_text = findViewById<android.widget.TextView>(R.id.new_user_text)
            new_user_text.setOnClickListener {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }

            val login_button = findViewById<android.widget.Button>(R.id.login_button)
            login_button.setOnClickListener {
                if (validateLoginDetails()) {
                    showProgressDialog()
                    logUser()
                }
            }

            val forgot_pwd_text = findViewById<android.widget.TextView>(R.id.forgot_pwd_text)
            forgot_pwd_text.setOnClickListener {
                val intent = Intent(this, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private fun validateLoginDetails(): Boolean {
        val email = findViewById<android.widget.EditText>(R.id.login_email_text)
        val password = findViewById<android.widget.EditText>(R.id.login_pwd_text)
        if(email.text.toString().isEmpty()){
            email.error = "Email is required"
            showErrorSnackbar(email.error as String, true)
            return false
        }
        if(password.text.toString().isEmpty()){
            password.error = "Password is required"
            showErrorSnackbar(password.error as String, true)
            return false
        }
        return true
    }

    private fun logUser() {
        val email = findViewById<android.widget.EditText>(R.id.login_email_text)
        val password = findViewById<android.widget.EditText>(R.id.login_pwd_text)

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    FirestoreClass().getUserInfo(this)
                }else {
                    hideProgressDialog()
                    showErrorSnackbar(task.exception!!.message.toString(), true)
                }
            }
    }

    fun userLoginSuccess(user: User){
        hideProgressDialog()
        if (yProgressDialog.isShowing()) {
            yProgressDialog.dismiss()
        }

        Toast.makeText(this, "User logged", Toast.LENGTH_SHORT).show()

        if (user.email==Constants.ADMIN_EMAIL){
            val intent = Intent(this, AddProductActivity::class.java)
            intent.putExtra(Constants.USER_DETAILS, user)
            startActivity(intent)
            finish()
        }else {
            if (user.profileCompleted == 0) {
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra(Constants.USER_DETAILS, user)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, BottomNavActivity::class.java)
                intent.putExtra(Constants.USER_DETAILS, user)
                startActivity(intent)
                finish()
            }
        }
    }

    fun userLoginFailed(){
        hideProgressDialog()
        Toast.makeText(this, "User login failed", Toast.LENGTH_SHORT).show()
    }
}