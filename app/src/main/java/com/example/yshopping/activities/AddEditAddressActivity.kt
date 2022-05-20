package com.example.yshopping.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.yshopping.R
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.Address
import com.example.yshopping.utils.Constants

class AddEditAddressActivity : BaseActivity() {

    private var mAddressDetails: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_add_edit_address)

        setActionBar()

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        if (mAddressDetails != null) {
            setAddressDetails()
        }

        val save_btn = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_save)
        save_btn.setOnClickListener {
            if(validateData()){
                saveAddressToFirestore()
            }
        }
    }

    private fun saveAddressToFirestore() {

        showProgressDialog()

        val name = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.full_name_text)
        val phone = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.phone_text)
        val address = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.address_text)
        val zipcode = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.zipcode_text)
        val note = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.note_text)

        val type: String = when{
            ActivityCompat.requireViewById<android.widget.RadioButton>(this, R.id.rb_home).isChecked -> Constants.HOME
            ActivityCompat.requireViewById<android.widget.RadioButton>(this, R.id.rb_office).isChecked -> Constants.OFFICE
            else -> Constants.OTHER
        }


        if (mAddressDetails == null && mAddressDetails?.id == null) {
            val addressModel = Address(
                FirestoreClass().getCurrentUserID(),
                name.text.toString(),
                phone.text.toString(),
                address.text.toString(),
                zipcode.text.toString(),
                note.text.toString(),
                type
            )
            FirestoreClass().addAddress(this,addressModel)
        } else {
            val addressModel = Address(
                FirestoreClass().getCurrentUserID(),
                name.text.toString(),
                phone.text.toString(),
                address.text.toString(),
                zipcode.text.toString(),
                note.text.toString(),
                type,
                mAddressDetails?.id!!
            )
            FirestoreClass().updateAddress(this, addressModel, mAddressDetails!!.id)
        }

    }

    private fun setAddressDetails() {
        val name = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.full_name_text)
        val phone = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.phone_text)
        val address = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.address_text)
        val zipcode = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.zipcode_text)
        val note = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.note_text)
        val rb_home = ActivityCompat.requireViewById<android.widget.RadioButton>(this, R.id.rb_home)
        val rb_office = ActivityCompat.requireViewById<android.widget.RadioButton>(this, R.id.rb_office)
        val rb_other = ActivityCompat.requireViewById<android.widget.RadioButton>(this, R.id.rb_other)

        name.setText(mAddressDetails?.name)
        phone.setText(mAddressDetails?.phone)
        address.setText(mAddressDetails?.address)
        zipcode.setText(mAddressDetails?.zipcode)
        note.setText(mAddressDetails?.notes)

        when(mAddressDetails?.type){
            Constants.HOME -> rb_home.isChecked = true
            Constants.OFFICE -> rb_office.isChecked = true
            else -> rb_other.isChecked = true
        }
    }

    fun addAddressSuccess(){
        hideProgressDialog()
        Toast.makeText(this, "Address modified successfully", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }

    fun addAddressFailed() {
        hideProgressDialog()
        Toast.makeText(this, "Failed to modify address", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("UseSupportActionBar")
    private fun setActionBar(){
        val toolbar = ActivityCompat.requireViewById<android.widget.Toolbar>(this, R.id.toolbar)
        setActionBar(toolbar)

        val actionBar = actionBar

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_white_24)
        }

        toolbar.setNavigationOnClickListener{onBackPressed()}
    }

    private fun validateData(): Boolean{
        val name = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.full_name_text)
        val phone = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.phone_text)
        val address = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.address_text)
        val zipcode = ActivityCompat.requireViewById<android.widget.EditText>(this, R.id.zipcode_text)

        if (name.text.toString().isEmpty()){
            name.error = "Full Name is required"
            showErrorSnackbar(name.error.toString(),true)
            return false
        }
        if(phone.text.toString().isEmpty()){
            phone.error = "Phone Number is required"
            showErrorSnackbar(phone.error.toString(), true)
            return false
        }
        if(phone.text.toString().length < 10 || phone.text.toString().length > 10){
            phone.error = "Phone Number should be 10 digits"
            showErrorSnackbar(phone.error.toString(), true)
            return false
        }
        if(address.text.toString().isEmpty()){
            address.error = "Address is required"
            showErrorSnackbar(address.error.toString(), true)
            return false
        }
        if(zipcode.text.toString().isEmpty()){
            zipcode.error = "Zipcode is required"
            showErrorSnackbar(zipcode.error.toString(), true)
            return false
        }
        return true
    }
}