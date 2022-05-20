package com.example.yshopping.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.example.yshopping.R
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.Order
import com.example.yshopping.models.Product
import com.example.yshopping.utils.Constants
import com.example.yshopping.utils.GlideLoader

class OrderDetailsActivity : BaseActivity() {

    private var mOrderId: String = ""
    private lateinit var mProductDetails: Product
    private lateinit var mOrderDetails: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_order_details)

        setActionBar()

        if (intent.hasExtra(Constants.EXTRA_ORDER_ID)) {
            mOrderId = intent.getStringExtra(Constants.EXTRA_ORDER_ID)!!
            showProgressDialog()
            FirestoreClass().getOrderDetails(this, mOrderId)
        }else{
            finish()
        }
    }

    @SuppressLint("SetTextI18n", "CutPasteId", "UseCompatLoadingForDrawables")
    fun orderDetailsSuccess(order: Order) {
        hideProgressDialog()
        mOrderDetails = order

        val toolbar = findViewById<TextView>(R.id.toolbar_title)
        toolbar.text = "Order:\n#" + order.id

        val orderSize = order.items.size
        var allNames = ""
        for (order_size in 0 until orderSize) {
            allNames = allNames + "\n" + order.items[order_size].title + " x " + order.items[order_size].cart_quantity
        }

        val productName = findViewById<TextView>(R.id.order_id_title)
        productName.text = allNames
        val orderSubtotal = findViewById<TextView>(R.id.order_subtotal)
        orderSubtotal.text = "Rs. " + order.sub_total
        val orderShipping = findViewById<TextView>(R.id.order_shipping)
        orderShipping.text = "Rs. " + order.shipping_charge
        val orderTotal = findViewById<TextView>(R.id.order_total)
        orderTotal.text = "Rs. " + order.total
        GlideLoader(this).loadProductImage(order.items[0].image.toUri(), imageView = findViewById(R.id.order_details_image))

        val shippingAddress = findViewById<TextView>(R.id.order_shipping_address)
        val billingAddress = findViewById<TextView>(R.id.order_billing_address)
        shippingAddress.text = order.address.name + ",\n" + order.address.address + ",\n" + order.address.zipcode + ",\n" + order.address.type
        billingAddress.text = order.address.name + ",\n" + order.address.address + ",\n" + order.address.zipcode + ",\n" + order.address.type

        val contactDetails = findViewById<TextView>(R.id.order_contact_details)
        contactDetails.text = order.address.phone + "\n"

        val orderStatus = findViewById<TextView>(R.id.order_status)
        orderStatus.text = order.order_status + " with " + order.payment_method

    }

    fun productDetailsSuccess(product: Product) {

        mProductDetails = product
//        val productName = findViewById<TextView>(R.id.product_name)
//        productName.text = product.title
//        val productPrice = findViewById<TextView>(R.id.product_price)
//        productPrice.text = "Rs. ${product.price}"
//        val productDescription = findViewById<TextView>(R.id.product_description)
//        productDescription.text = product.desc

    }

    fun orderDetailsFailed(){
        hideProgressDialog()

        Toast.makeText(this, "Failed to get order details", Toast.LENGTH_SHORT).show()
        finish()
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
}