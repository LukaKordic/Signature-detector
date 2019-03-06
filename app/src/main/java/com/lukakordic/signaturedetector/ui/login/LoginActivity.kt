package com.lukakordic.signaturedetector.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.lukakordic.signaturedetector.R
import com.lukakordic.signaturedetector.ui.ContentActivity
import com.lukakordic.signaturedetector.utils.RC_SIGN_IN
import com.lukakordic.signaturedetector.utils.onClick
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val currentUser by lazy { GoogleSignIn.getLastSignedInAccount(this) }
    private val signInOptions by lazy { GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build() }
    private val signInClient by lazy { GoogleSignIn.getClient(this, signInOptions) }

    companion object {
        fun launch(context: Context) = context.startActivity(Intent(context, LoginActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initUI()
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
        launchRecognition.onClick { SignatureBottomSheet.newInstance().show(supportFragmentManager, null) }
    }

    private fun signIn() {
        val intent = signInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            ContentActivity.launch(this)
            finish()
        }
    }
}
