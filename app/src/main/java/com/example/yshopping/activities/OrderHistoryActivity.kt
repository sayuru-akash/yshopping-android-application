package com.example.yshopping.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.yshopping.R
import com.example.yshopping.adapters.MyOrdersListAdapter
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.Order

class OrderHistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_order_history)

        setActionBar()
    }

    override fun onResume() {
        super.onResume()
        getMyOrders()
    }

    fun populateOrderHistory(ordersList: ArrayList<Order>){
        hideProgressDialog()

        val order_history_recycler_view = findViewById<RecyclerView>(R.id.order_history_recycler_view)
        val empty_orders_text = findViewById<TextView>(R.id.empty_orders_text)
        if (ordersList.size > 0){
            order_history_recycler_view.visibility = View.VISIBLE
            empty_orders_text.visibility = View.GONE

            order_history_recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
            order_history_recycler_view.setHasFixedSize(true)

            val orderHistoryAdapter = MyOrdersListAdapter(this, ordersList)
            order_history_recycler_view.adapter = orderHistoryAdapter

        }else{
            order_history_recycler_view.visibility = View.GONE
            empty_orders_text.visibility = View.VISIBLE
        }
    }

    private fun getMyOrders(){
        showProgressDialog()
        FirestoreClass().getUserOrders(this)
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