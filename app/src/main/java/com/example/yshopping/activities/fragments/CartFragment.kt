package com.example.yshopping.activities.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yshopping.R
import com.example.yshopping.activities.AddressListActivity
import com.example.yshopping.adapters.CartListAdapter
import com.example.yshopping.databinding.FragmentCartBinding
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.CartItem
import com.example.yshopping.models.Product
import com.example.yshopping.utils.Constants

class CartFragment : BaseFragment() {

    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartList: ArrayList<CartItem>

    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        getProductsList()

        binding.productCheckoutButton.setOnClickListener {
            if (mCartList.size > 0) {
                val intent = Intent(activity, AddressListActivity::class.java)
                intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, true)
                startActivity(intent)
            } else {
                Toast.makeText(activity, "Cart is empty or out of stock. Please add items to checkout.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCartItemsList(Fragment: Fragment) {
        showProgressDialog()
        FirestoreClass().getCartList(Fragment)
    }

    private fun getProductsList(){
        showProgressDialog()
        FirestoreClass().getAllProductsList(this)
    }

    @SuppressLint("SetTextI18n")
    fun cartListSuccess(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in mProductList) {
            for (cartItem in cartList) {
                if (product.id == cartItem.product_id) {
                    cartItem.stock_quantity = product.quantity
                    if (product.quantity.toInt()==0){
                        cartItem.cart_quantity = product.quantity
                        Toast.makeText(context, "One or more products in your cart may be out of stock. These items have been reduced to 0", Toast.LENGTH_LONG).show()
                    }
                    else if (cartItem.stock_quantity.toInt() < cartItem.cart_quantity.toInt()) {
                        cartItem.cart_quantity = cartItem.stock_quantity
                        Toast.makeText(context, "One or more products in your cart may have less stock. The quantity of these products are updated", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        mCartList = cartList


        if (mCartList.size>0){
            binding.cartRecyclerView.visibility = View.VISIBLE
            binding.emptyCartText.visibility = View.GONE
            binding.cartRecyclerView.layoutManager = LinearLayoutManager(activity)
            binding.cartRecyclerView.setHasFixedSize(true)

            var subTotal: Double = 0.0
            var shippingCost: Double = 0.0
            for (item in mCartList) {
                val availableQty = item.stock_quantity.toInt()
                if (availableQty>0){
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                    shippingCost += (90.0 * quantity)
                }
            }
            val total = subTotal+shippingCost
            binding.productDetailsSubtotalValue.text = "Rs. $subTotal"
            binding.productDetailsShippingValue.text = "Rs. $shippingCost"
            binding.productDetailsTotalValue.text = "Rs. $total"

            val adapterCart = CartListAdapter(requireActivity(),mCartList,true)
            binding.cartRecyclerView.adapter = adapterCart
        }else{
            binding.cartRecyclerView.visibility = View.GONE
            binding.emptyCartText.visibility = View.VISIBLE
        }

    }

    fun cartListFailed() {
        hideProgressDialog()
    }

    fun productListSuccess(productsList: ArrayList<Product>){
        hideProgressDialog()
        mProductList = productsList
        getCartItemsList(this)
    }

    fun productsListFailed(){
        hideProgressDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}