package com.lukakordic.signaturedetector

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class ContentActivity : AppCompatActivity() {

    private val signInOptions by lazy { GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build() }
    private val signInClient by lazy { GoogleSignIn.getClient(this, signInOptions) }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, ContentActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.signOut -> signInClient.signOut().addOnCompleteListener {
                LoginActivity.launch(this)
                finish()
            }
        }
        return false
    }
}
