package com.example.yshopping.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Address (
    var user_id: String = "",
    var name: String = "",
    var phone: String = "",
    var address: String = "",
    var zipcode: String = "",
    var notes: String = "",
    val type: String = "",
    var id: String = ""
) : Parcelable