package com.sun.todo.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ConstantLocale")
private val  outPutDateFormat= SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
    timeZone= TimeZone.getTimeZone("UTC")
}

@BindingAdapter("setPriority")
fun setPriority(view: TextView, priority :Int){
    when(priority){
        0 ->{
           view.text="Low Priority"
            //parseColor("#FFEB3B")
            view.setTextColor(Color.GREEN)
        }
        1 ->{
            view.text="Medium Priority" //#F1C40F
            view.setTextColor(Color.parseColor("#FFDC00"))
        }
        2 ->{
            view.text="High Priority"
            view.setTextColor(Color.RED)
        }
    }
}
@BindingAdapter("setDateTime")
fun setDateTime(view: TextView, date: Long){
    view.text = outPutDateFormat.format(date)
}
@SuppressLint("SimpleDateFormat")
@BindingAdapter("setHour")
fun setHour(view: TextView, hour: Int){
    view.text = DecimalFormat("00:").format(hour)
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("setMin")
fun setMin(view: TextView, min: Int){
    view.text = DecimalFormat("00").format(min)
}

@BindingAdapter("makeVisible")
fun setVisibility(view: ImageView, st: Boolean){
    when(st){
        true -> {
            view.visibility = VISIBLE
        }
        false ->{
            view.visibility = INVISIBLE
        }
    }
}