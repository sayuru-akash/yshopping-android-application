package com.example.yshopping.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.yshopping.R
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.utils.Constants
import com.example.yshopping.utils.GlideLoader
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageUri: Uri? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_add_product)
        setActionBar()

        val product_image_add = findViewById<ImageView>(R.id.product_image_add)
        product_image_add.setOnClickListener(this)
        val btn_add = findViewById<Button>(R.id.btn_add)
        btn_add.setOnClickListener(this)
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

    override fun onClick(p0: View?) {
        if (p0!=null){
            when(p0.id){
                R.id.product_image_add -> {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
                    }
                }
                R.id.btn_add -> {
                    if (validateProductDetails()){
                        uploadProductToFirestore()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted.
                showErrorSnackbar("Read Storage Permission granted", false)
                Constants.showImageChooser(this)
            } else {
                // Permission is denied.
                showErrorSnackbar("Oops you just denied read storage permission. You can enable it from the settings", true)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data != null && data.data != null){
                try {
                    mSelectedImageUri = data.data!!
                    val product_image = findViewById<android.widget.ImageView>(R.id.product_image)
                    GlideLoader(this).loadUserImage(mSelectedImageUri!!, product_image)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Image Selection Failed!", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Image Selection Failed!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Image Selection Failed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateProductDetails(): Boolean{
        val product_name = findViewById<EditText>(R.id.pro_title)
        val product_price = findViewById<EditText>(R.id.pro_price)
        val product_description = findViewById<EditText>(R.id.pro_desc)
        val product_quantity = findViewById<EditText>(R.id.pro_qty)

        if (product_name.text.isEmpty()){
            product_name.error = "Product name is required"
            showErrorSnackbar(product_name.error.toString(), true)
            return false
        }
        if (product_price.text.isEmpty()){
            product_price.error = "Product price is required"
            showErrorSnackbar(product_price.error.toString(), true)
            return false
        }
        if (product_quantity.text.isEmpty()){
            product_quantity.error = "Product quantity is required"
            showErrorSnackbar(product_quantity.error.toString(), true)
            return false
        }
        if (product_description.text.isEmpty()){
            product_description.error = "Product description is required"
            showErrorSnackbar(product_description.error.toString(), true)
            return false
        }
        if (mSelectedImageUri == null){
            showErrorSnackbar("Product image is required", true)
            return false
        }
        return true
    }

    private fun uploadProductToFirestore(){
        val productHashMap = HashMap<String, Any>()

        val product_name = findViewById<EditText>(R.id.pro_title)
        val product_price = findViewById<EditText>(R.id.pro_price)
        val product_description = findViewById<EditText>(R.id.pro_desc)
        val product_quantity = findViewById<EditText>(R.id.pro_qty)

        productHashMap["title"] = product_name.text.toString()
        productHashMap["price"] = product_price.text.toString()
        productHashMap["desc"] = product_description.text.toString()
        productHashMap["quantity"] = product_quantity.text.toString()

        showProgressDialog()

        if (mSelectedImageUri!=null){
            productHashMap[Constants.PRODUCT_IMAGE] = FirestoreClass().uploadProductImageToCloud(this, mSelectedImageUri, productHashMap)
        }
        else{
            FirestoreClass().addProduct(this, productHashMap)
        }
    }

    fun addProductSuccess(){
        hideProgressDialog()
        showErrorSnackbar("Product added successfully", false)
        Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, AddProductActivity::class.java))
        finish()
    }

    fun addProductFailed(){
        hideProgressDialog()
        showErrorSnackbar("Product adding failed!", true)
        Log.e("AddProduct", "Product adding failed!")
    }

}