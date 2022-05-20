package com.example.yshopping.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class YSRadioButton(context: Context, attributeSet: AttributeSet): AppCompatRadioButton(context, attributeSet) {
    init {
        applyFont()
    }

    private fun applyFont(){
        val typeface: Typeface = Typeface.createFromAsset(context.assets, "fonts/proxima_bold.ttf")
        super.setTypeface(typeface)
    }
}