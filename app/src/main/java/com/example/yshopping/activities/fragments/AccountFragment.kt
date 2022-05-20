package com.example.yshopping.activities.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.example.yshopping.activities.AddressListActivity
import com.example.yshopping.activities.LoginActivity
import com.example.yshopping.activities.OrderHistoryActivity
import com.example.yshopping.activities.UserProfileActivity
import com.example.yshopping.databinding.FragmentAccountBinding
import com.example.yshopping.firestore.FirestoreClass
import com.example.yshopping.models.User
import com.example.yshopping.utils.Constants
import com.example.yshopping.utils.GlideLoader
import kotlinx.coroutines.delay

class AccountFragment : BaseFragment() {

    private var _binding: FragmentAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        showProgressDialog()
        FirestoreClass().getUser(this)
    }
    fun userSuccess(user: User) {
        hideProgressDialog()
        binding.headerTitleName.text = user.firstname
        context?.let { GlideLoader(it).loadUserImage(user.image.toUri(), binding.profileImage) }

        binding.detailsTitle.setOnClickListener{
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            intent.putExtra(Constants.USER_DETAILS, user)
            startActivity(intent)
        }
        binding.logoutTitle.setOnClickListener {
            showProgressDialog()
            FirestoreClass().logoutUser()
            Toast.makeText(context, "Logout Successfully", Toast.LENGTH_SHORT).show()
            Intent(context, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                hideProgressDialog()
                startActivity(it)
            }
        }
        binding.addressesTitle.setOnClickListener {
            val intent = Intent(requireContext(), AddressListActivity::class.java)
            intent.putExtra(Constants.USER_DETAILS, user)
            startActivity(intent)
        }
        binding.ordersTitle.setOnClickListener {
            val intent = Intent(requireContext(), OrderHistoryActivity::class.java)
            intent.putExtra(Constants.USER_DETAILS, user)
            startActivity(intent)
        }
    }
    fun userFailed(){
        hideProgressDialog()
        Toast.makeText(context, "Failed to get user", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )    : View {

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}