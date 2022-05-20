package com.example.yshopping.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.example.yshopping.R
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_register)

        val existing_user_text = findViewById<android.widget.TextView>(R.id.existing_user_text)
        existing_user_text.setOnClickListener {
            val intent = android.content.Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        setActionBar()

        val reg_button = findViewById<android.widget.Button>(R.id.reg_button)
        reg_button.setOnClickListener{
            if(validateRegisterDetails()){
                showProgressDialog()
                registerUser()
            }
        }
    }

    @SuppressLint("UseSupportActionBar")
    private fun setActionBar(){
        val toolbar = ActivityCompat.requireViewById<Toolbar>(this, R.id.toolbar)
        setActionBar(toolbar)

        val actionBar = actionBar

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_white_24)
        }

        toolbar.setNavigationOnClickListener{onBackPressed()}
    }

    private fun validateRegisterDetails(): Boolean {
        val fname = findViewById<android.widget.EditText>(R.id.reg_fname_text)
        val lname = findViewById<android.widget.EditText>(R.id.reg_lname_text)
        val email = findViewById<android.widget.EditText>(R.id.reg_email_text)
        val password = findViewById<android.widget.EditText>(R.id.reg_pwd_text)
        val confirm_password = findViewById<android.widget.EditText>(R.id.reg_pwd_check_text)
        val checkbox = findViewById<android.widget.CheckBox>(R.id.reg_check_terms)

        if(fname.text.isEmpty()) {
            fname.error = "First Name is required"
            showErrorSnackbar(fname.error as String, true)
            return false
        }

        if(lname.text.isEmpty()) {
            lname.error = "Last Name is required"
            showErrorSnackbar(lname.error as String, true)
            return false
        }

        if(email.text.isEmpty()) {
            email.error = "Email is required"
            showErrorSnackbar(email.error as String, true)
            return false
        }

        if(password.text.isEmpty()) {
            password.error = "Password is required"
            showErrorSnackbar(password.error as String, true)
            return false
        }

        if(confirm_password.text.isEmpty()) {
            confirm_password.error = "Confirm password is required"
            showErrorSnackbar(confirm_password.error as String, true)
            return false
        }

        if(password.text.toString() != confirm_password.text.toString()) {
            confirm_password.error = "Passwords does not match"
            showErrorSnackbar(confirm_password.error as String, true)
            return false
        }

        if(!checkbox.isChecked) {
            checkbox.error = "Please accept terms and conditions"
            showErrorSnackbar(checkbox.error as String, true)
            return false
        }
        return true
    }

    private fun registerUser() {
        val fname = findViewById<android.widget.EditText>(R.id.reg_fname_text)
        val lname = findViewById<android.widget.EditText>(R.id.reg_lname_text)
        val email = findViewById<android.widget.EditText>(R.id.reg_email_text)
        val password = findViewById<android.widget.EditText>(R.id.reg_pwd_text)

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    val User = User(
                        firebaseUser.uid,
                        fname.text.toString(),
                        lname.text.toString(),
                        firebaseUser.email!!,
                    )

                    FirestoreClass().registerUser(this,User)

                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }else {
                    hideProgressDialog()
                    showErrorSnackbar(task.exception!!.message.toString(), true)
                }
            }
    }

    fun userRegistrationSuccess(){
        hideProgressDialog()
        Toast.makeText(this, "User registration successful", Toast.LENGTH_SHORT).show()
    }

    fun userRegistrationFailed(){
        hideProgressDialog()
        Toast.makeText(this, "User registration failed", Toast.LENGTH_SHORT).show()
    }
}