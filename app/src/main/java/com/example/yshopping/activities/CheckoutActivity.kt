package com.example.yshopping.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.yshopping.R
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.Address
import com.example.yshopping.models.CartItem
import com.example.yshopping.models.Mail
import com.example.yshopping.models.Product
import com.example.yshopping.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.paypal.android.sdk.payments.*
import org.json.JSONException
import java.math.BigDecimal
import java.util.*


class CheckoutActivity : BaseActivity() {

    private var mAddressDetails: Address? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var shippingCost: Double = 0.0
    private var mTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_checkout)
        setActionBar()

        if (intent.hasExtra(Constants.ADDRESS_KEY)) {
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.ADDRESS_KEY)
        }

        if (mAddressDetails != null) {
            setAddressDetails()
            getProductList()
        }

        val btn_payment = findViewById<Button>(R.id.btn_payment)
        btn_payment.setOnClickListener {
            val rb_paypal = findViewById<RadioButton>(R.id.rb_paypal)
            val rb_cod = findViewById<RadioButton>(R.id.rb_cod)

            if (rb_paypal.isChecked) {
                payWithPaypal()
            }
            else if (rb_cod.isChecked) {
                payWithCash()
            }else{
                Toast.makeText(this, "Please select payment method", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun cartListSuccess(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in mProductList) {
            for (cartItem in cartList) {
                if (product.id == cartItem.product_id) {
                    val newStock: Int = product.quantity.toInt() - cartItem.cart_quantity.toInt()
                    cartItem.stock_quantity = product.quantity
                    product.quantity = newStock.toString()
                }
            }
        }

        mCartItemList = cartList

        //val cartListAdapter = CartListAdapter(this, mCartItemList, false)

        for (item in mCartItemList) {
            val availableQty = item.stock_quantity.toInt()
            if (availableQty > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += price * quantity
                shippingCost += (90.0 * quantity)
            }
        }
        val checkout_sub_total_amount =
            findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.checkout_sub_total_amount)
        checkout_sub_total_amount.text = "Rs. $mSubTotal"
        val checkout_shipping_amount =
            findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.checkout_shipping_amount)
        checkout_shipping_amount.text = "Rs. $shippingCost"

        if (mSubTotal > 0) {
            val btn_payment = findViewById<Button>(R.id.btn_payment)
            btn_payment.visibility = android.view.View.VISIBLE
            mTotal = mSubTotal + shippingCost
            val checkout_total_amount =
                findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.checkout_total_amount)
            checkout_total_amount.text = "Rs. $mTotal"
        } else {
            val btn_payment = findViewById<Button>(R.id.btn_payment)
            btn_payment.visibility = android.view.View.GONE
        }
    }

    fun cartListFailure() {
        hideProgressDialog()
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    fun successProductList(productList: ArrayList<Product>) {
        mProductList = productList
        getCartItemsList()
    }

    fun failureProductList() {
        hideProgressDialog()
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    private fun getProductList() {
        showProgressDialog()
        FirestoreClass().getAllProductsList(this)
    }

    private fun getCartItemsList() {
        FirestoreClass().getCartList(this)
    }

    private fun payWithCash() {
        showProgressDialog()
        val order = com.example.yshopping.models.Order(
            FirestoreClass().getCurrentUserID(),
            mCartItemList,
            mAddressDetails!!,
            mSubTotal.toString(),
            shippingCost.toString(),
            mTotal.toString(),
            "Cash on Delivery",
            "Placed"
        )
        FirestoreClass().placeOrder(this, order)
    }

    private fun placeAnOrderPP() {
        showProgressDialog()
        val order = com.example.yshopping.models.Order(
            FirestoreClass().getCurrentUserID(),
            mCartItemList,
            mAddressDetails!!,
            mSubTotal.toString(),
            shippingCost.toString(),
            mTotal.toString(),
            "PayPal",
            "Placed"
        )
        FirestoreClass().placeOrder(this, order)
    }

    fun placeOrderSuccess() {
        FirestoreClass().updateProductOnCart(this, mCartItemList, mProductList)
    }

    fun updateCartSuccess() {
        //send an email to the user with the order details
        val currentUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        var mail = currentUser.email!!.toString()
        var subject = ("New Order Placed")
        var text = ("A new order was placed successfully for user: \n${currentUser.email} \n \nOrder Details: \n \nShipping Address: \n${mAddressDetails?.name}\n${mAddressDetails?.address} \n${mAddressDetails?.phone}\n\nSubTotal: ${mSubTotal.toString()}\nTotal Amount: \n${mTotal.toString()}")
        var msgList = HashMap<String, Any>()
        msgList["subject"] = subject
        msgList["text"] = text
        val order = Mail(
            to = mail,
            message = msgList
        )

        FirestoreClass().sendMail(this, order)
    }

    fun mailSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, BottomNavActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun setAddressDetails() {
        val name_text = findViewById<android.widget.TextView>(R.id.name_text)
        val delivery_address_text =
            findViewById<android.widget.TextView>(R.id.delivery_address_text)
        val delivery_address_type_text =
            findViewById<android.widget.TextView>(R.id.delivery_address_type_text)
        val delivery_address_note_text =
            findViewById<android.widget.TextView>(R.id.delivery_address_note_text)
        val delivery_mobile_text = findViewById<android.widget.TextView>(R.id.delivery_mobile_text)

        name_text.text = mAddressDetails?.name
        delivery_address_text.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipcode}"
        delivery_address_type_text.text = mAddressDetails?.type
        delivery_address_note_text.text = mAddressDetails?.notes
        delivery_mobile_text.text = mAddressDetails?.phone

        if (mAddressDetails!!.notes == "") {
            delivery_address_note_text.visibility = android.view.View.GONE
        }

    }

    @SuppressLint("UseSupportActionBar")
    private fun setActionBar() {
        val toolbar = ActivityCompat.requireViewById<android.widget.Toolbar>(this, R.id.toolbar)
        setActionBar(toolbar)

        val actionBar = actionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_white_24)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    @Suppress("DEPRECATION")
    fun payWithPaypal() {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         */
        val thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE, mTotal.toString())
        val intent = Intent(this, PaymentActivity::class.java)

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, Constants.config)

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy)

        startActivityForResult(intent, Constants.REQUEST_CODE_PAYMENT)
    }
    private fun getThingToBuy(paymentIntent: String, amount: String): PayPalPayment {
        return PayPalPayment(
            BigDecimal(amount), "USD", "cart items",
            paymentIntent)
    }
    private val oauthScopes: PayPalOAuthScopes
        get() {
            val scopes = HashSet(
                Arrays.asList(
                    PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL,
                    PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS
                )
            )
            return PayPalOAuthScopes(scopes)
        }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK) {
                val confirm = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirm != null) {
                    try {
                        Log.i(Constants.TAG, confirm.toJSONObject().toString(4))
                        Log.i(Constants.TAG, confirm.payment.toJSONObject().toString(4))
                        placeAnOrderPP()
                    } catch (e: JSONException) {
                        Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show()
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "The user canceled.", Toast.LENGTH_LONG).show()
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.", Toast.LENGTH_LONG).show()
            }
    }

    //private fun sendAuthorizationToServer(authorization: PayPalAuthorization) {

        /**
         * Send the authorization response to your server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         */

    //}
}