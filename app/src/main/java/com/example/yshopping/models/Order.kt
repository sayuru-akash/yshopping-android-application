package com.example.yshopping.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Order (
    val user_id: String = "",
    val items: ArrayList<CartItem> = ArrayList(),
    val address: Address = Address(),
    val sub_total: String = "",
    val shipping_charge: String = "",
    val total: String = "",
    val payment_method: String = "",
    val order_status: String = "",
    var id: String = ""
): Parcelable