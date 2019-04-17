package com.lukakordic.signaturedetector.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.lukakordic.signaturedetector.R
import com.lukakordic.signaturedetector.ui.ContentActivity
import com.lukakordic.signaturedetector.utils.RC_CAPTURE_IMAGE
import com.lukakordic.signaturedetector.utils.RC_SIGN_IN
import com.lukakordic.signaturedetector.utils.onClick
import kotlinx.android.synthetic.main.activity_login.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class LoginActivity : AppCompatActivity() {
  
  private val currentUser by lazy { GoogleSignIn.getLastSignedInAccount(this) }
  private val signInOptions by lazy { GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build() }
  private val signInClient by lazy { GoogleSignIn.getClient(this, signInOptions) }
  private lateinit var tflite: Interpreter
  
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
      takePhoto()
//      SignatureBottomSheet.newInstance().show(supportFragmentManager, null)
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
    val fileDescriptor = assets.openFd("fashion.tflite")
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
  }
  
  private fun takePhoto() {
    startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), RC_CAPTURE_IMAGE)
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
          //todo prepare image and run recognition
        }
      }
    }
  }
}
