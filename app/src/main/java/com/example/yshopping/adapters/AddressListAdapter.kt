package com.example.yshopping.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.yshopping.R
import com.example.yshopping.activities.AddEditAddressActivity
import com.example.yshopping.activities.AddressListActivity
import com.example.yshopping.activities.CheckoutActivity
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.Address
import com.example.yshopping.utils.Constants

open class AddressListAdapter (private val context: Context, private var list: ArrayList<Address>, private val selectAddress: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return AddressViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.address_list_layout,
                parent,
                false
            )
        )

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is AddressViewHolder) {
            holder.itemView.findViewById<TextView>(R.id.address_item_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.address_item_phone).text = model.phone
            holder.itemView.findViewById<TextView>(R.id.address_item_address).text = model.address +", "+ model.zipcode
            holder.itemView.findViewById<TextView>(R.id.address_item_type).text = model.type

            if (selectAddress){
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra(Constants.ADDRESS_KEY, model)
                    context.startActivity(intent)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(activity, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }

    fun notifyDeleteItem(activity: AddressListActivity, position: Int) {
        FirestoreClass().deleteAddress(context,list[position].id)
    }

    private class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}