package com.lukakordic.signaturedetector.utils

import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity

inline fun View.onClick(crossinline onClick: () -> Unit) {
  this.setOnClickListener { onClick() }
}

fun FragmentActivity.toast(text: String) {
  Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}