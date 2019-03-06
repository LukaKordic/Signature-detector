package com.lukakordic.signaturedetector.utils

import android.view.View

inline fun View.onClick(crossinline onClick: () -> Unit) {
    this.setOnClickListener { onClick() }
}