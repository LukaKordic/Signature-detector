package com.lukakordic.signaturedetector.ml

import java.lang.StringBuilder

data class Result(val id: String?, val title: String?, val confidence: Float) {
  override fun toString(): String {
    val stringBuilder = StringBuilder()
    if (title != null) stringBuilder.append("$title ")
    stringBuilder.append("${confidence * 100.0}")
    return stringBuilder.toString()
  }
}