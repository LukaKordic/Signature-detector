package com.lukakordic.signaturedetector

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val currentUser by lazy { GoogleSignIn.getLastSignedInAccount(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        val signInClient = GoogleSignIn.getClient(this, signInOptions)

        googleSignInBtn.setOnClickListener {
            if (currentUser == null)
                ContentActivity.launch(this)
        }
    }
}
