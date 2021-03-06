package com.sun.todo.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MSPTextViewLight (context: Context, attrs: AttributeSet):AppCompatTextView(context, attrs) {
    init {
        applyFont()
    }

    private fun applyFont(){
        val typeface: Typeface = Typeface.createFromAsset(context.assets, "fonts/proxima_light.ttf")
        super.setTypeface(typeface)
    }
}
