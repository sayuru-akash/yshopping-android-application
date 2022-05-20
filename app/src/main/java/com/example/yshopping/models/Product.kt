package com.example.yshopping.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Product(
    var id: String = "",
    val title: String = "",
    val price: String = "",
    val desc: String = "",
    val product_image: String = "",
    var quantity: String = ""
): Parcelable