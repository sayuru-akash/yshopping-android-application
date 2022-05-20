package com.example.yshopping.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User(
    val id: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val email: String = "",
    val image: String = "",
    val phone: Long = 0,
    val gender: String = "",
    val profileCompleted: Int = 0
): Parcelable