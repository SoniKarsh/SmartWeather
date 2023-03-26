package com.technobugsai.smartweather.utils.extensions

import android.content.Context
import android.widget.Toast

/**
 * Extension method to show toast for Context.
 */
fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    this?.let { Toast.makeText(it, text, duration).show() }
