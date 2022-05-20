package com.example.yshopping.firestore

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.yshopping.activities.*
import com.example.yshopping.activities.fragments.AccountFragment
import com.example.yshopping.activities.fragments.CartFragment
import com.example.yshopping.activities.fragments.HomeFragment
import com.example.yshopping.models.*
import com.example.yshopping.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {
    private val db = FirebaseFirestore.getInstance()

    //fun getDb() = db

    fun registerUser(activity: RegisterActivity, userInfo: User){
        db.collection(Constants.USERS).document(userInfo.id).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener {
                activity.userRegistrationFailed()
            }
    }

    fun updateUser(activity: UserProfileActivity, userHashMap: HashMap<String, Any>){
        db.collection(Constants.USERS).document(FirebaseAuth.getInstance().currentUser!!.uid).update(userHashMap)
            .addOnSuccessListener {
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener {
                activity.profileUpdateFailed()
            }
    }

    fun addProduct(activity: AddProductActivity, productHashMap: HashMap<String, Any>) {
        db.collection(Constants.PRODUCTS).add(productHashMap)
            .addOnSuccessListener {
                val productId = it.id
                db.collection(Constants.PRODUCTS).document(productId).update(
                    "id", productId
                ).addOnSuccessListener {
                    activity.addProductSuccess()
                }.addOnFailureListener {
                    activity.addProductFailed()
                }
                    .addOnFailureListener {
                        activity.addProductFailed()
                    }
            }
    }

        fun getCurrentUserID(): String {
            val currentUser = FirebaseAuth.getInstance().currentUser

            var currentUserID = ""
            if (currentUser != null) {
                currentUserID = currentUser.uid
            }
            return currentUserID
        }

        @SuppressLint("CommitPrefEdits")
        fun getUserInfo(activity: Activity) {
            db.collection(Constants.USERS).document(getCurrentUserID()).get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)!!

                    val sharedPreferences = activity.getSharedPreferences(
                        Constants.YSHOPPING_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreferences.edit()
                    editor.putString(
                        Constants.LOGGED_IN_USERNAME,
                        "${user.firstname} ${user.lastname}"
                    )
                    editor.apply()

                    when (activity) {
                        is LoginActivity -> {
                            activity.userLoginSuccess(user)
                        }
                    }
                }
                .addOnFailureListener {
                    when (activity) {
                        is LoginActivity -> {
                            activity.userLoginFailed()
                        }
                    }
                }
        }

        fun uploadImageToCloud(activity: UserProfileActivity, imageFileURI: Uri?) {
            val imageRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                Constants.PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(
                    activity,
                    imageFileURI
                )
            )

            imageRef.putFile(imageFileURI!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener {
                        val userHashMap = HashMap<String, Any>()
                        userHashMap[Constants.PROFILE_IMAGE_LOC] = it.toString()

                        updateUser(activity, userHashMap)
                    }
                }
                .addOnFailureListener {
                    activity.profileUpdateFailed()
                }
        }

        fun uploadProductImageToCloud(
            activity: AddProductActivity,
            imageFileURI: Uri?,
            productHashMap: HashMap<String, Any>
        ) {
            val imageRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                Constants.PRODUCT_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(
                    activity,
                    imageFileURI
                )
            )

            imageRef.putFile(imageFileURI!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener {
                        productHashMap[Constants.PRODUCT_IMAGE] = it.toString()

                        addProduct(activity, productHashMap)
                    }
                }
                .addOnFailureListener {
                    activity.addProductFailed()
                }
        }

        fun getProductsList(fragment: Fragment) {
            db.collection(Constants.PRODUCTS).get()
                .addOnSuccessListener {
                    val productsList = ArrayList<Product>()
                    for (document in it) {
                        val product = document.toObject(Product::class.java)
                        productsList.add(product)
                    }
                    when (fragment) {
                        is HomeFragment -> {
                            fragment.productListSuccess(productsList)
                        }
                    }
                }
                .addOnFailureListener {
                    when (fragment) {
                        is HomeFragment -> {
                            fragment.productsListFailed()
                        }
                    }
                }
        }

        fun getProductDetails(activity: Activity, productId: String) {
            db.collection(Constants.PRODUCTS).document(productId).get()
                .addOnSuccessListener {
                    val product = it.toObject(Product::class.java)
                    if (product != null) {
                        when (activity) {
                            is ProductDetailsActivity -> {
                                activity.productDetailsSuccess(product)
                            }
                            is OrderDetailsActivity -> {
                                activity.productDetailsSuccess(product)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    when (activity) {
                        is ProductDetailsActivity -> {
                            activity.productDetailsFailed()
                        }
                        is OrderDetailsActivity -> {
                            activity.orderDetailsFailed()
                        }
                    }
                }
        }

        fun getAllProductsList(fragment: CartFragment) {
            db.collection(Constants.PRODUCTS).get()
                .addOnSuccessListener {
                    val productsList = ArrayList<Product>()
                    for (document in it) {
                        val product = document.toObject(Product::class.java)
                        product.id = document.id
                        productsList.add(product)
                    }
                    fragment.productListSuccess(productsList)
                }
                .addOnFailureListener {
                    fragment.productsListFailed()
                }
        }

        fun getAllProductsList(activity: Activity) {
            db.collection(Constants.PRODUCTS).get()
                .addOnSuccessListener {
                    val productsList = ArrayList<Product>()
                    for (document in it) {
                        val product = document.toObject(Product::class.java)
                        product.id = document.id
                        productsList.add(product)
                    }
                    when(activity){
                        is CheckoutActivity -> {
                            activity.successProductList(productsList)
                        }
                    }
                }
                .addOnFailureListener {
                    when(activity){
                        is CheckoutActivity -> {
                            activity.failureProductList()
                        }
                    }
                }
        }

        fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {
            db.collection(Constants.CART_ITEMS).add(addToCart)
                .addOnSuccessListener {
                    val documentId = it.id
                    db.collection(Constants.CART_ITEMS).document(documentId).update(
                        "id", documentId
                    ).addOnSuccessListener {
                        db.collection(Constants.PRODUCTS).document(addToCart.product_id).get()
                            .addOnSuccessListener {
                                val valueQty: String = it.data!!["quantity"]!!.toString()
                                db.collection(Constants.CART_ITEMS).document(documentId).update(
                                    "stock_quantity", valueQty
                                ).addOnSuccessListener {
                                    activity.addCartItemSuccess()
                                }.addOnFailureListener() {
                                    activity.addCartItemFailed()
                                }
                            }.addOnFailureListener {
                                activity.addCartItemFailed()
                            }
                    }.addOnFailureListener {
                        activity.addCartItemFailed()
                    }
                .addOnFailureListener {
                    activity.addCartItemFailed()
                }
        }}

        fun updateCartItems(context: Context,cartId: String, itemHashMap: HashMap<String, Any>) {
            db.collection(Constants.CART_ITEMS).document(cartId).update(itemHashMap)
                .addOnSuccessListener {
                    Toast.makeText(context, "Cart Updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Cart Update Failed", Toast.LENGTH_SHORT).show()
                }
        }

        fun deleteCartItems(context: Context,cartItemId: String) {
            db.collection(Constants.CART_ITEMS).document(cartItemId).delete()
                .addOnSuccessListener {
                    (context).let {
                        Toast.makeText(
                            it,
                            "Item deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {
                    (context).let {
                        Toast.makeText(
                            it,
                            "Item deletion failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        fun checkIfItemExistsInCart(activity: ProductDetailsActivity, productId: String) {
            db.collection(Constants.CART_ITEMS).whereEqualTo(Constants.USER_ID, getCurrentUserID()).whereEqualTo(Constants.PRODUCT_ID, productId)
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        activity.itemNotInCart()
                    } else {
                        activity.itemAlreadyInCart()
                    }
                }
                .addOnFailureListener {
                    activity.itemNotInCart()
                }
        }

        fun getCartList(fragment: Fragment) {
            db.collection(Constants.CART_ITEMS).whereEqualTo(Constants.USER_ID, getCurrentUserID()).get()
                .addOnSuccessListener {
                    val cartList = ArrayList<CartItem>()

                    for (document in it) {
                        val cartItem = document.toObject(CartItem::class.java)
                        cartList.add(cartItem)
                    }
                    when (fragment) {
                        is CartFragment -> {
                            fragment.cartListSuccess(cartList)
                        }
                    }
                }
                .addOnFailureListener {
                    when (fragment) {
                        is CartFragment -> {
                            fragment.cartListFailed()
                        }
                    }
                }
        }

        fun getCartList(activity: Activity) {
            db.collection(Constants.CART_ITEMS).whereEqualTo(Constants.USER_ID, getCurrentUserID()).get()
                .addOnSuccessListener {
                    val cartList = ArrayList<CartItem>()

                    for (document in it) {
                        val cartItem = document.toObject(CartItem::class.java)
                        cartList.add(cartItem)
                    }
                    when (activity) {
                        is CheckoutActivity -> {
                            activity.cartListSuccess(cartList)
                        }
                    }
                }
                .addOnFailureListener {
                    when (activity) {
                        is CheckoutActivity -> {
                            activity.cartListFailure()
                        }
                    }
                }
        }

        fun addAddress(activity: AddEditAddressActivity, address: Address) {
            db.collection(Constants.ADDRESSES).add(address)
                .addOnSuccessListener {
                    val id = it.id
                    db.collection(Constants.ADDRESSES).document(id).update("id", id)
                        .addOnSuccessListener {
                            activity.addAddressSuccess()
                        }
                        .addOnFailureListener {
                            activity.addAddressFailed()
                        }
                }
                .addOnFailureListener {
                    activity.addAddressFailed()
                }
        }

        fun getAddressesList(activity: AddressListActivity) {
            db.collection(Constants.ADDRESSES).whereEqualTo(Constants.USER_ID, getCurrentUserID()).get()
                .addOnSuccessListener {
                    val addressesList = ArrayList<Address>()

                    for (document in it) {
                        val address = document.toObject(Address::class.java)
                        addressesList.add(address)
                    }
                    activity.addressesListSuccess(addressesList)
                }
                .addOnFailureListener {
                    activity.addressesListFailed()
                }
        }

        fun updateAddress(activity: AddEditAddressActivity, address: Address, addressId: String) {
            db.collection(Constants.ADDRESSES).document(addressId).set(address, SetOptions.merge())
                .addOnSuccessListener {
                    activity.addAddressSuccess()
                }
                .addOnFailureListener {
                    activity.addAddressFailed()
                }
        }

        fun deleteAddress(context: Context, addressId: String) {
            db.collection(Constants.ADDRESSES).document(addressId).delete()
                .addOnSuccessListener {
                    if (context is AddressListActivity) {
                        context.deleteAddressSuccess(context)
                    }
                }
                .addOnFailureListener {
                    context.let {
                        Toast.makeText(
                            it,
                            "Failed to delete item",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        fun updateProductOnCart(activity: CheckoutActivity, cartList: ArrayList<CartItem>, productList: ArrayList<Product>) {
            val writeBatch = db.batch()

            for (cartItem in cartList) {

                for (product in productList) {
                    val productHashmap = HashMap<String, Any>()
                    if (cartItem.product_id==product.id){
                        productHashmap[Constants.STOCK_QUANTITY] = product.quantity
                        val documentReference = db.collection(Constants.PRODUCTS).document(cartItem.product_id)
                        writeBatch.update(documentReference, productHashmap)
                    }else{
                        // do nothing
                    }
                }
            }

            for (cartItem in cartList) {
                val documentReference = db.collection(Constants.CART_ITEMS).document(cartItem.id)
                writeBatch.delete(documentReference)
            }

            writeBatch.commit()
                .addOnSuccessListener {
                    activity.updateCartSuccess()
                }
                .addOnFailureListener {
                    activity.hideProgressDialog()
                    Toast.makeText(
                        activity,
                        "Some error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        fun placeOrder(activity: CheckoutActivity, order: Order) {
            db.collection(Constants.ORDERS).add(order)
                .addOnSuccessListener {
                    val id = it.id
                    db.collection(Constants.ORDERS).document(id).update("id", id)
                        .addOnSuccessListener {
                            activity.placeOrderSuccess()
                        }
                        .addOnFailureListener {
                            activity.hideProgressDialog()
                            Toast.makeText(
                                activity,
                                "Failed to place order",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .addOnFailureListener {
                    activity.hideProgressDialog()
                    Toast.makeText(
                        activity,
                        "Failed to place order",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        fun getUserOrders(activity: OrderHistoryActivity) {
            db.collection(Constants.ORDERS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get()
                .addOnSuccessListener {
                    val orderList : ArrayList<Order> = ArrayList()
                    for (document in it) {
                        val order = document.toObject(Order::class.java)
                        order.id = document.id
                        orderList.add(order)
                    }
                    activity.populateOrderHistory(orderList)
                }
                .addOnFailureListener {
                    activity.hideProgressDialog()
                    Toast.makeText(
                        activity,
                        "Failed to fetch orders",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        fun getOrderDetails(activity: OrderDetailsActivity, orderId: String) {
            db.collection(Constants.ORDERS).document(orderId).get()
                .addOnSuccessListener {
                    val order = it.toObject(Order::class.java)
                    if (order != null) {
                        activity.orderDetailsSuccess(order)
                    }
                }
                .addOnFailureListener {
                    activity.orderDetailsFailed()
                }
        }

        fun getUser(fragment: Fragment) {
            db.collection(Constants.USERS).document(getCurrentUserID()).get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)!!

                    when (fragment) {
                        is AccountFragment -> {
                            fragment.userSuccess(user)
                        }
                    }
                }
                .addOnFailureListener {
                    when (fragment) {
                        is AccountFragment -> {
                            fragment.userFailed()
                        }
                    }
                }
        }


    fun sendMail(activity: Activity, arrayList: Mail) {


        db.collection("mail")
            .add(arrayList)
            .addOnSuccessListener {
                when (activity) {
                    is CheckoutActivity -> {
                        activity.mailSuccess()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Error sending mail", Toast.LENGTH_SHORT).show()
            }
    }

        fun logoutUser() {
            FirebaseAuth.getInstance().signOut()
        }

    }
