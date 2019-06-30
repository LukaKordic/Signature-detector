package com.lukakordic.signaturedetector.utils

import android.graphics.*
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity

inline fun View.onClick(crossinline onClick: () -> Unit) {
  this.setOnClickListener { onClick() }
}

fun FragmentActivity.toast(text: String) {
  Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Bitmap.toGreyscale(): Bitmap {
  val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  val c = Canvas(bmpGrayscale)
  val paint = Paint()
  val cm = ColorMatrix().apply { setSaturation(0f) }
  val filter = ColorMatrixColorFilter(cm)
  paint.colorFilter = filter
  c.drawBitmap(this, 0f, 0f, paint)
  return bmpGrayscale
}