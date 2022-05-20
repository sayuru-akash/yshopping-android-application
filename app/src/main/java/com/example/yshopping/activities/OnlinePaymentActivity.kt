package com.example.yshopping.activities

import android.content.Intent
import android.os.Bundle
import com.example.yshopping.R



class OnlinePaymentActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_online_payment)
    }}
//
//    // Paypal Configuration Object
//    private val config = PayPalConfiguration() // Start with mock environment.  When ready,
//        // switch to sandbox (ENVIRONMENT_SANDBOX)
//        // or live (ENVIRONMENT_PRODUCTION)
//        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // on below line we are passing a client id.
//        .clientId(clientKey)
//
//    private fun getPayment() {
//
//        // Creating a paypal payment on below line.
//        val payment = PayPalPayment(
//            10.00.toBigDecimal(), "USD", "Course Fees",
//            PayPalPayment.PAYMENT_INTENT_SALE
//        )
//
//        // Creating Paypal Payment activity intent
//        val intent = Intent(this, PaymentActivity::class.java)
//
//        //putting the paypal configuration to the intent
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
//
//        // Putting paypal payment to the intent
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
//
//        // Starting the intent activity for result
//        // the request code will be used on the method onActivityResult
//        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
//    }
//
//    protected override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        data: Intent?
//    ) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // If the result is from paypal
//        if (requestCode == PAYPAL_REQUEST_CODE) {
//
//            // If the result is OK i.e. user has not canceled the payment
//            if (resultCode == RESULT_OK) {
//
//                // Getting the payment confirmation
//                val confirm: PaymentConfirmation? =
//                    data?.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
//
//                // if confirmation is not null
//                if (confirm != null) {
//                    try {
//                        // Getting the payment details
//                        val paymentDetails = confirm.toJSONObject().toString(4)
//                        // on below line we are extracting json response and displaying it in a text view.
//                        val payObj = JSONObject(paymentDetails)
//                        val payID = payObj.getJSONObject("response").getString("id")
//                        val state = payObj.getJSONObject("response").getString("state")
//                       // paymentTV.setText("Payment $state\n with payment id is $payID")
//                    } catch (e: JSONException) {
//                        // handling json exception on below line
//                        Log.e("Error", "an extremely unlikely failure occurred: ", e)
//                    }
//                }
//            } else if (resultCode == RESULT_CANCELED) {
//                // on below line we are checking the payment status.
//                Log.i("paymentExample", "The user canceled.")
//            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
//                // on below line when the invalid paypal config is submitted.
//                Log.i(
//                    "paymentExample",
//                    "An invalid Payment or PayPalConfiguration was submitted. Please see the docs."
//                )
//            }
//        }
//    }
//}