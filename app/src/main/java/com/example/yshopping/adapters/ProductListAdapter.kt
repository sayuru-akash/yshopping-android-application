package com.example.yshopping.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil.bind
import androidx.recyclerview.widget.RecyclerView
import com.example.yshopping.R
import com.example.yshopping.activities.ProductDetailsActivity
import com.example.yshopping.models.Product
import com.example.yshopping.utils.Constants
import com.example.yshopping.utils.GlideLoader


open class ProductListAdapter(private val context: Context, private var list: ArrayList<Product>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.product_list_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is ViewHolder) {
            val product_image = (R.id.product_image)
            val product_name = (R.id.product_name)
            val product_price = (R.id.product_price)

            GlideLoader(context).loadProductImage(model.product_image.toUri(), holder.itemView.findViewById(product_image))
            holder.itemView.findViewById<TextView>(product_name).text = model.title
            holder.itemView.findViewById<TextView>(product_price).text = "Rs. ${model.price}"

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.id)
                context.startActivity(intent)
            }
        }
    }

    private fun bind(holder: ProductListAdapter.ViewHolder) {

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}