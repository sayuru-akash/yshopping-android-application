package com.example.yshopping.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.yshopping.R
import com.example.yshopping.activities.BottomNavActivity
import com.example.yshopping.activities.fragments.CartFragment
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.CartItem
import com.example.yshopping.utils.Constants
import com.example.yshopping.utils.GlideLoader

open class CartListAdapter (private val context: Context, private var list: ArrayList<CartItem>, private val updateCartItems: Boolean)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.cart_item_layout,
                parent,
                false
            )
        )
    }

    private fun bind(holder: ViewHolder) {

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            val product_cart_quantity = (R.id.product_cart_quantity)
            val qty_decrease_button = (R.id.qty_decrease_button)
            val qty_increase_button = (R.id.qty_increase_button)

            GlideLoader(context).loadProductImage(model.image.toUri(), holder.itemView.findViewById(product_image))
            holder.itemView.findViewById<TextView>(product_name).text = model.title
            holder.itemView.findViewById<TextView>(product_price).text = "Rs. ${model.price}"
            //holder.itemView.findViewById<TextView>(product_cart_quantity).text = model.cart_quantity

            if (!updateCartItems){
                holder.itemView.findViewById<ImageButton>(qty_decrease_button).visibility = View.GONE
                holder.itemView.findViewById<ImageButton>(qty_increase_button).visibility = View.GONE
                holder.itemView.findViewById<ImageView>(R.id.delete_cart_item_image).visibility = View.GONE
            }else {
                if (model.stock_quantity.toInt() == 0) {
                    holder.itemView.findViewById<ImageButton>(qty_decrease_button).visibility =
                        View.GONE
                    holder.itemView.findViewById<ImageButton>(qty_increase_button).visibility =
                        View.GONE
                    holder.itemView.findViewById<TextView>(product_cart_quantity).text =
                        "Out of Stock"
                    holder.itemView.findViewById<TextView>(product_cart_quantity)
                        .setTextColor(context.resources.getColor(R.color.red_700))
                } else {
                    holder.itemView.findViewById<ImageButton>(qty_decrease_button).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<ImageButton>(qty_increase_button).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<TextView>(product_cart_quantity).text =
                        model.cart_quantity
                    holder.itemView.findViewById<TextView>(product_cart_quantity)
                        .setTextColor(context.resources.getColor(R.color.black))
                }


                if (model.cart_quantity.toInt() == 1) {
                    holder.itemView.findViewById<ImageButton>(qty_decrease_button).visibility =
                        View.GONE
                }
                if (model.cart_quantity.toInt() == model.stock_quantity.toInt()) {
                    holder.itemView.findViewById<ImageButton>(qty_increase_button).visibility =
                        View.GONE
                }

                holder.itemView.findViewById<ImageView>(R.id.delete_cart_item_image)
                    .setOnClickListener {
                        val dialog = AlertDialog.Builder(context)
                        dialog.setTitle("Delete Item")
                        dialog.setMessage("Are you sure you want to delete this item?")
                        dialog.setPositiveButton("Yes") { _, _ ->
                            FirestoreClass().deleteCartItems(context, model.id)
                            val intent = Intent(context, BottomNavActivity::class.java)
                            intent.putExtra(Constants.EXTRA_GO_TO_CART, "true")
                            context.startActivity(intent)
                        }
                        dialog.setNegativeButton("No") { _, _ ->
                            // do nothing
                        }
                        dialog.show()

                    }
                holder.itemView.findViewById<ImageButton>(qty_decrease_button).setOnClickListener {
                    if (model.cart_quantity.toInt() > 1) {
                        model.cart_quantity = (model.cart_quantity.toInt() - 1).toString()
                        holder.itemView.findViewById<ImageButton>(qty_increase_button).visibility =
                            View.VISIBLE
                        holder.itemView.findViewById<TextView>(product_cart_quantity).text =
                            model.cart_quantity

                        val cartQty = model.cart_quantity.toInt()
                        val itemHashMap = HashMap<String, Any>()
                        itemHashMap[Constants.CART_QUANTITY] = cartQty.toString()
                        FirestoreClass().updateCartItems(context, model.id, itemHashMap)
                        val intent = Intent(context, BottomNavActivity::class.java)
                        intent.putExtra(Constants.EXTRA_GO_TO_CART, "true")
                        context.startActivity(intent)

                    } else {
                        holder.itemView.findViewById<ImageButton>(qty_decrease_button).visibility =
                            View.GONE
                    }
                }
                holder.itemView.findViewById<ImageButton>(qty_increase_button).setOnClickListener {
                    if (model.stock_quantity.toInt() > model.cart_quantity.toInt()) {
                        model.cart_quantity = (model.cart_quantity.toInt() + 1).toString()
                        holder.itemView.findViewById<ImageButton>(qty_decrease_button).visibility =
                            View.VISIBLE
                        holder.itemView.findViewById<TextView>(product_cart_quantity).text =
                            model.cart_quantity

                        val cartQty = model.cart_quantity.toInt()
                        val itemHashMap = HashMap<String, Any>()
                        itemHashMap[Constants.CART_QUANTITY] = cartQty.toString()
                        FirestoreClass().updateCartItems(context, model.id, itemHashMap)
                        val intent = Intent(context, BottomNavActivity::class.java)
                        intent.putExtra(Constants.EXTRA_GO_TO_CART, "true")
                        context.startActivity(intent)

                    } else {
                        holder.itemView.findViewById<ImageButton>(qty_increase_button).visibility =
                            View.GONE
                    }
                }
            }

        }
    }
}