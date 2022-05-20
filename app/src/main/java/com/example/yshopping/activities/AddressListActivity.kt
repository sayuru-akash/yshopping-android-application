package com.example.yshopping.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yshopping.R
import com.example.yshopping.adapters.AddressListAdapter
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.Address
import com.example.yshopping.utils.Constants
import com.example.yshopping.utils.SwipeToDeleteCallback
import com.example.yshopping.utils.SwipeToEditCallback


class AddressListActivity : BaseActivity() {

    private var mSelectedAddress: Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_address_list)

        setActionBar()

        showProgressDialog()
        getAddressList()


        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mSelectedAddress = intent.getBooleanExtra(Constants.EXTRA_SELECTED_ADDRESS, false)
        }


        if (mSelectedAddress) {
            val toolbar_title = findViewById<android.widget.TextView>(R.id.toolbar_title)
            toolbar_title.text = "Select Shipping Address"

            val add_address_button = findViewById<android.widget.Button>(R.id.add_address_button)
            add_address_button.text = "Modify Saved Addresses"
            @Suppress("DEPRECATION")
            add_address_button.setOnClickListener {
                val intent = Intent(this, AddressListActivity::class.java)
                startActivity(intent)
            }
        }else{
            val add_address_button = findViewById<android.widget.Button>(R.id.add_address_button)
            @Suppress("DEPRECATION")
            add_address_button.setOnClickListener {
                val intent = Intent(this, AddEditAddressActivity::class.java)
                startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            showProgressDialog()
            getAddressList()
        }
    }

//    override fun onResume() {
//        super.onResume()
//        showProgressDialog()
//        getAddressList()
//    }

    private fun getAddressList(){
        FirestoreClass().getAddressesList(this)
    }

    fun addressesListSuccess(addressList : ArrayList<Address>) {

        hideProgressDialog()

        val empty_addresses_text = findViewById<android.widget.TextView>(R.id.empty_addresses_text)
        val addresses_recycler_view = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.addresses_recycler_view)

        if (addressList.size>0){

            empty_addresses_text.visibility = android.view.View.GONE
            addresses_recycler_view.visibility = android.view.View.VISIBLE

            addresses_recycler_view.layoutManager = LinearLayoutManager(this)
            addresses_recycler_view.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this, addressList, mSelectedAddress)
            addresses_recycler_view.adapter = addressAdapter

            if (!mSelectedAddress){
                val editSwipeHandler = object: SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = addresses_recycler_view.adapter as AddressListAdapter
                        adapter.notifyEditItem(this@AddressListActivity,viewHolder.adapterPosition)
                    }
                }
                val deleteSwipeHandler = object: SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog()
                        val adapter = addresses_recycler_view.adapter as AddressListAdapter
                        adapter.notifyDeleteItem(this@AddressListActivity,viewHolder.adapterPosition)
                    }
                }

                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(addresses_recycler_view)
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(addresses_recycler_view)
            }

        }else{
            empty_addresses_text.visibility = android.view.View.VISIBLE
            addresses_recycler_view.visibility = android.view.View.GONE
        }

    }

    fun addressesListFailed() {
        hideProgressDialog()
        showErrorSnackbar("Failed to load addresses",true)
    }

    fun deleteAddressSuccess(context: Context){
        Toast.makeText(context, "Address deleted", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, AddressListActivity::class.java)
        startActivity(intent)
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