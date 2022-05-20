package com.example.yshopping.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.yshopping.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadUserImage(imageURI: Uri, imageView: ImageView){
        try{
            Glide.with(context)
                .load (imageURI)
                .centerCrop()
                .placeholder(R.drawable.profile_image_placeholder)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadProductImage(imageURI: Uri, imageView: ImageView){
        try{
            Glide.with(context)
                .load (imageURI)
                .centerCrop()
                .placeholder(R.drawable.product_image_placeholder)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}