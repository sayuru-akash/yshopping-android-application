package com.example.yshopping.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MSPTextViewRegular (context: Context, attrs: AttributeSet):AppCompatTextView(context, attrs) {
    init {
        applyFont()
    }

    private fun applyFont(){
        val typeface: Typeface = Typeface.createFromAsset(context.assets, "fonts/proxima_regular.ttf")
        super.setTypeface(typeface)
    }
}
