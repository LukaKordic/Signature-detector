package com.lukakordic.signaturedetector.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.lukakordic.signaturedetector.R
import com.lukakordic.signaturedetector.ml.ImageClassifier
import com.lukakordic.signaturedetector.ml.Result
import com.lukakordic.signaturedetector.ui.ContentActivity
import com.lukakordic.signaturedetector.utils.*
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_login.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class LoginActivity : AppCompatActivity() {
  
  private val currentUser by lazy { GoogleSignIn.getLastSignedInAccount(this) }
  private val signInOptions by lazy {
    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
  }
  private val signInClient by lazy { GoogleSignIn.getClient(this, signInOptions) }
  private lateinit var tflite: Interpreter
  private val signatureClassifier by lazy { ImageClassifier(assets) }
  
  companion object {
    fun launch(context: Context) = context.startActivity(Intent(context, LoginActivity::class.java))
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    initUI()
    initTensorFlow()
  }
  
  private fun initUI() {
    initGoogleLogin()
    setListeners()
  }
  
  private fun initGoogleLogin() {
    if (currentUser != null) {
      ContentActivity.launch(this)
      finish()
    } else {
      googleSignInBtn.setOnClickListener {
        signIn()
      }
    }
  }
  
  private fun setListeners() {
    launchRecognition.onClick {
      startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), RC_CAPTURE_IMAGE)
    }
  }
  
  private fun signIn() {
    val intent = signInClient.signInIntent
    startActivityForResult(intent, RC_SIGN_IN)
  }
  
  private fun initTensorFlow() {
    try {
      tflite = Interpreter(loadModelFile(), Interpreter.Options())
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
  
  private fun loadModelFile(): MappedByteBuffer {
    val fileDescriptor = assets.openFd(MODEL_NAME)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
  }
  
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
      when (requestCode) {
        RC_SIGN_IN -> {
          ContentActivity.launch(this)
          finish()
        }
        RC_CAPTURE_IMAGE -> {
          val image = data?.extras?.get("data") as Bitmap
          val resizedImage = Bitmap.createScaledBitmap(image, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, false)
          photoPreview.setImageBitmap(resizedImage)
          runInference(resizedImage)
        }
      }
    }
  }
  
  private fun runInference(image: Bitmap) {
    signatureClassifier.recognizeSignature(image).subscribe(object : SingleObserver<List<Result>>{
      override fun onSuccess(t: List<Result>) {
        result.text = t.toString()
      }
  
      override fun onSubscribe(d: Disposable) {
//        toast("not implemented")
      }
  
      override fun onError(e: Throwable) {
        result.text = e.toString()
      }
    })
  }
}
