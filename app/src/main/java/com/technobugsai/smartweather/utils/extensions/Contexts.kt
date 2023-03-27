package com.technobugsai.smartweather.utils.extensions

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    this?.let { Toast.makeText(it, text, duration).show() }

fun View.showSnack(msg: String){
    Snackbar.make(
        this,
        msg,
        Snackbar.LENGTH_SHORT
    ).show()
}
