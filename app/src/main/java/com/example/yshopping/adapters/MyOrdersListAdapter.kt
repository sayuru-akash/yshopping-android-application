package com.example.yshopping.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yshopping.R
import com.example.yshopping.activities.OrderDetailsActivity
import com.example.yshopping.activities.ProductDetailsActivity
import com.example.yshopping.models.Order
import com.example.yshopping.utils.Constants

open class MyOrdersListAdapter(private val context: Context, private var list: ArrayList<Order>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyOrdersListAdapter.OrdersViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.cart_item_layout,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is OrdersViewHolder) {
            holder.itemView.findViewById<TextView>(R.id.product_name).text = "Order ID: ${model.id}"
            holder.itemView.findViewById<TextView>(R.id.product_price).text = "Total amount: ${model.total}"
            holder.itemView.findViewById<TextView>(R.id.product_cart_quantity).text = "Total Quantity: "+ model.items.size.toString()
            holder.itemView.findViewById<ImageButton>(R.id.qty_decrease_button).visibility = View.GONE
            holder.itemView.findViewById<ImageButton>(R.id.qty_increase_button).visibility = View.GONE
            holder.itemView.findViewById<ImageView>(R.id.delete_cart_item_image).visibility = View.GONE
            holder.itemView.findViewById<ImageView>(R.id.product_image).visibility = View.GONE

            holder.itemView.setOnClickListener {
                val intent = Intent(context, OrderDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_ORDER_ID, model.id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}