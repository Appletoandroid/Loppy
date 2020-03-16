package com.appleto.loppyuser.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.helper.PrefUtils
import com.google.firebase.FirebaseApp

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        Handler().postDelayed({
            if (PrefUtils.getBooleanValue(this, Const.IS_LOGGED_IN)) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
