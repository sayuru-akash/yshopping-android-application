package com.example.yshopping.activities.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yshopping.adapters.ProductListAdapter
import com.example.yshopping.databinding.FragmentHomeBinding
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.Product

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    fun productListSuccess(productsList: ArrayList<Product>) {
        hideProgressDialog()

        if(productsList.size > 0) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
            binding.recyclerView.layoutManager = GridLayoutManager(activity,2)
            binding.recyclerView.setHasFixedSize(true)

            val adapterProducts = ProductListAdapter(requireActivity(),productsList)
            binding.recyclerView.adapter = adapterProducts
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        }
    }

    fun getProductListFromFirestore(){
        showProgressDialog()
        FirestoreClass().getProductsList(this)
    }

    fun productsListFailed() {
        hideProgressDialog()
        //binding.productList.visibility = View.GONE
        //binding.productListError.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFirestore()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}