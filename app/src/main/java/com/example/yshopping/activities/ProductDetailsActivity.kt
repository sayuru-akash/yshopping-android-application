package com.example.yshopping.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.RED
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.yshopping.R
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.CartItem
import com.example.yshopping.models.Product
import com.example.yshopping.utils.Constants
import com.example.yshopping.utils.GlideLoader

class ProductDetailsActivity : BaseActivity() {

    private var mProductId: String = ""
    private lateinit var mProductDetails: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_product_details)

        setActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            //Log.i("ProductDetailId", mProductId)
            showProgressDialog()
            FirestoreClass().getProductDetails(this, mProductId)
        }else{
            finish()
        }

        val home_icon = findViewById<ImageView>(R.id.home_icon)
        home_icon.setOnClickListener {
            Intent(this, BottomNavActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        val add_to_cart_button = findViewById<Button>(R.id.add_to_cart_button)
        add_to_cart_button.setOnClickListener {
            addToCart()
        }

        val go_to_cart = findViewById<Button>(R.id.go_to_cart_button)
        go_to_cart.setOnClickListener {
            val intent = Intent(this, BottomNavActivity::class.java)
            intent.putExtra(Constants.EXTRA_GO_TO_CART, "true")
            startActivity(intent)
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
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back)
        }

        val cart_button = findViewById<ImageView>(R.id.cart_button)
        cart_button.setOnClickListener {
            val intent = Intent(this, BottomNavActivity::class.java)
            intent.putExtra(Constants.EXTRA_GO_TO_CART, "true")
            startActivity(intent)
        }

        toolbar.setNavigationOnClickListener{onBackPressed()}
    }

    @SuppressLint("SetTextI18n", "CutPasteId", "UseCompatLoadingForDrawables")
    fun productDetailsSuccess(product: Product) {
        hideProgressDialog()
        mProductDetails = product
        GlideLoader(this).loadProductImage(product.product_image.toUri(), imageView = findViewById(R.id.product_image))
        val productName = findViewById<TextView>(R.id.product_name)
        productName.text = product.title
        val productPrice = findViewById<TextView>(R.id.product_price)
        productPrice.text = "Rs. ${product.price}"
        val productDescription = findViewById<TextView>(R.id.product_description)
        productDescription.text = product.desc

        if (product.quantity.toInt() == 0) {
            findViewById<Button>(R.id.add_to_cart_button).text = "Out of Stock"
            findViewById<Button>(R.id.add_to_cart_button).isEnabled = false
            findViewById<Button>(R.id.add_to_cart_button).background=resources.getDrawable(R.drawable.button_disabled)
        }else {
            FirestoreClass().checkIfItemExistsInCart(this, mProductId)
        }
    }

    fun productDetailsFailed(){
        hideProgressDialog()

        Toast.makeText(this, "Failed to get product details", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun addToCart(){
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.product_image,
            Constants.DEFAULT_CART_QUANTITY,
            "",
            Constants.CART_ITEMS
        )
        showProgressDialog()
        FirestoreClass().addCartItems(this, cartItem)
    }

    fun addCartItemSuccess(){
        hideProgressDialog()
        showErrorSnackbar("Item added to cart", false)
        val add_to_cart_button = findViewById<Button>(R.id.add_to_cart_button)
        add_to_cart_button.visibility = View.GONE
        val go_to_cart_button = findViewById<Button>(R.id.go_to_cart_button)
        go_to_cart_button.visibility = View.VISIBLE
    }

    fun addCartItemFailed(){
        hideProgressDialog()
        showErrorSnackbar("Failed to add item to cart", true)
    }

    fun itemAlreadyInCart(){
        hideProgressDialog()
        val add_to_cart_button = findViewById<Button>(R.id.add_to_cart_button)
        add_to_cart_button.visibility = View.GONE
        val go_to_cart_button = findViewById<Button>(R.id.go_to_cart_button)
        go_to_cart_button.visibility = View.VISIBLE
    }

    fun itemNotInCart(){
        hideProgressDialog()
    }
}