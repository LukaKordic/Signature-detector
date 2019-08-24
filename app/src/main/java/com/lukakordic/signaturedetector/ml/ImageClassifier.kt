package com.lukakordic.signaturedetector.ml

import android.content.res.AssetManager
import android.graphics.Bitmap
import com.lukakordic.signaturedetector.utils.*
import io.reactivex.Single
import org.tensorflow.lite.Interpreter
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

class ImageClassifier(private val assetManager: AssetManager) {
  
  private val interpreter by lazy { Interpreter(loadModelFile(assetManager, MODEL_NAME), Interpreter.Options()) }
  private var labelProb: Array<FloatArray>
  private val labels = Vector<String>()
  private val intValues by lazy { IntArray(INPUT_SIZE * INPUT_SIZE) }
  private var imgData: ByteBuffer
  
  init {
    try {
      val reader = BufferedReader(InputStreamReader(assetManager.open(LABEL_NAME)) as Reader?)
      reader.use {
        while (true) {
          val line = reader.readLine() ?: break
          labels.add(line)
        }
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }
    
    labelProb = Array(1) { FloatArray(labels.size) }
    imgData = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE)
    imgData.order(ByteOrder.nativeOrder())
  }
  
  private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
    imgData.rewind()
    bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    var pixel = 0
    for (i in 0 until DIM_IMG_SIZE_X) {
      for (j in 0 until DIM_IMG_SIZE_Y) {
        val value = intValues[pixel++]
        val r = (value shr 16 and 0xFF)
        val g = (value shr 8 and 0xFF)
        val b = (value and 0xFF)
        val grey = ((r + g + b) / 3) / 255.0f
        imgData.putFloat(grey)
      }
    }
  }
  
  private fun loadModelFile(assetManager: AssetManager, model_name: String): MappedByteBuffer {
    val fileDescriptor = assetManager.openFd(model_name)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
  }
  
  fun recognizeSignature(bitmap: Bitmap): Single<List<Result>> {
    return Single.just(bitmap).flatMap {
      convertBitmapToByteBuffer(it)
      interpreter.run(imgData, labelProb) //todo close interpreter
      val pq = PriorityQueue(2,
                             Comparator<Result> { lhs, rhs ->
                               // Intentionally reversed to put high confidence at the head of the queue.
                               (rhs.confidence).compareTo(lhs.confidence)
                             })
      for (i in labels.indices) {
        pq.add(Result("$i", if (labels.size > i) labels[i] else "unknown", labelProb[0][i]))
      }
      val recognitions = ArrayList<Result>()
      val recognitionsSize = min(pq.size, MAX_RESULTS)
      for (i in 0 until recognitionsSize) recognitions.add(pq.poll())
      
      return@flatMap Single.just(recognitions)
    }
  }
}